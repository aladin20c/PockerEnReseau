package Server.ServerGameStates;

import Game.Card;
import Game.Player;
import Game.utils.Request;
import Server.ClientHandler;
import Server.Server;
import Server.Room;

public class Playing5CardPokerState extends GameState{

    private Player player;


    public Playing5CardPokerState(ClientHandler clientHandler , Room room) {
        super(clientHandler, room);
        this.player=room.getGame().getPlayer(clientHandler.getClientUsername());
        startGame();
    }


    @Override
    public void analyseRequest(String messageFromClient) {
        if(!messageFromClient.matches("\\d\\d\\d.+")) {

            broadCastMessageToEveryone(clientHandler.getClientUsername()+":"+messageFromClient);

        }else if(messageFromClient.matches(Request.FOLD)){

            if(!room.getGame().canFold(player)){
                writeToClient(Request.INVALID);
            }else {
                player.fold(room.getGame());
                clientHandler.cancelTask(messageFromClient);//(╯°□°)╯︵ ┻━┻
                broadCastTask(Request.ACTION_RECIEVED);//(╯°□°)╯︵ ┻━┻
                broadCastMessage("510 " + clientHandler.getClientUsername() + " FOLD");
                writeToClient("400 ACCEPTED");
                rotateTurn();
            }

        }else if(messageFromClient.matches(Request.CHECK)){

            if(!room.getGame().canCheck(player)){
                writeToClient(Request.INVALID);
            }else{
                player.check(room.getGame());
                clientHandler.cancelTask(messageFromClient);//(╯°□°)╯︵ ┻━┻
                broadCastTask(Request.ACTION_RECIEVED);//(╯°□°)╯︵ ┻━┻
                broadCastMessage("511 "+ clientHandler.getClientUsername() +" CHECK");
                writeToClient("400 ACCEPTED");
                rotateTurn();
            }

        }else if(messageFromClient.matches(Request.CALL)){

            if(!room.getGame().canCall(player)){
                writeToClient(Request.INVALID);
            }else{
                player.call(room.getGame());
                clientHandler.cancelTask(messageFromClient);//(╯°□°)╯︵ ┻━┻
                broadCastTask(Request.ACTION_RECIEVED);//(╯°□°)╯︵ ┻━┻
                broadCastMessage("512 "+ clientHandler.getClientUsername() +" CALL");
                writeToClient("400 ACCEPTED");
                rotateTurn();
            }

        }else if(messageFromClient.matches(Request.RAISE)) {

            int raise = Integer.parseInt(messageFromClient.substring(10));
            if(!room.getGame().canRaise(player,raise)){
                writeToClient(Request.INVALID);
            }else {
                player.raise(room.getGame(), raise);//(╯°□°)╯︵ ┻━┻
                clientHandler.cancelTask(messageFromClient);//(╯°□°)╯︵ ┻━┻
                broadCastTask(Request.ACTION_RECIEVED);
                broadCastMessage("513 " + clientHandler.getClientUsername() + " RAISE " + raise);
                writeToClient("400 ACCEPTED");
                rotateTurn();
            }

        }else if(messageFromClient.matches(Request.ACTION_RECIEVED)){

            clientHandler.cancelTask(messageFromClient);//(╯°□°)╯︵ ┻━┻

        }else if(messageFromClient.matches(Request.CARDS_RECIEVED)){


            clientHandler.cancelTask(messageFromClient);//(╯°□°)╯︵ ┻━┻

        }else if(messageFromClient.matches(Request.CHANGE)){

            String[] data=messageFromClient.split("\\s+");
            int numberOfCards=Integer.parseInt(data[2]);
            Card[] cards=new Card[numberOfCards];
            for(int i=3;i<data.length;i++){
                cards[i-3]=new Card(data[i]);
            }

            if(data.length!=numberOfCards+3 || !room.getGame().canChange(player,cards)){
                writeToClient(Request.ERROR);
            }else{
                Card[] newCards=room.getGame().change(player,cards);
                writeToClient("700 ACCEPTED");
                //(╯°□°)╯︵ ┻━┻
                broadCastTask(Request.CHANGE_RECIEVED);
                broadCastMessage("720 "+clientHandler.getClientUsername()+" Change "+numberOfCards);
                String cardDistribution="610 CARDS ";
                cardDistribution+=(data[2]);
                for(Card card:newCards){
                    cardDistribution=cardDistribution+" "+card.toString();
                }
                //(╯°□°)╯︵ ┻━┻
                clientHandler.addTask(Request.CARDS_RECIEVED);
                writeToClient(cardDistribution);
                rotateTurn();
            }


        }else if(messageFromClient.matches(Request.CHANGE_RECIEVED)){

            clientHandler.cancelTask(messageFromClient);//(╯°□°)╯︵ ┻━┻

        }else if (messageFromClient.matches(Request.QUIT)) {

            clientQuit();
            rotateTurn();

        } else if (messageFromClient.matches(Request.QUIT_RECIEVED)) {

            clientHandler.cancelTask(messageFromClient);//(╯°□°)╯︵ ┻━┻

        }else if(messageFromClient.matches(Request.GETSTATE)) {

            writeToClient("666 PlayingTexasHoldemState");

        }else if(messageFromClient.matches(Request.GETALLCARDS)) {

            leakCards();

        }else if(messageFromClient.matches(Request.GETPLAYERS)) {

            StringBuilder playerList= new StringBuilder("666 " + room.getGame().getPlayers().size() + " PLAYERS");
            for(Player player : room.getGame().getPlayers()){
                playerList.append(" ").append(player.getName());
            }
            writeToClient(playerList.toString());

        }else if(messageFromClient.matches(Request.GET_ACTIVE_PLAYERS)) {

            StringBuilder playerList= new StringBuilder("666 " + room.getGame().getPlayers().size() + " PLAYERS");
            for(ClientHandler ch : room.getClientHandlers()){
                playerList.append(" ").append(ch.getClientUsername());
            }
            writeToClient(playerList.toString());

        }else{

            clientHandler.writeToClient(Request.ERROR);

        }
    }


    @Override
    public void clientQuit() {
        writeToClient(Request.QUIT_ACCEPTED);
        broadCastTask(Request.QUIT_RECIEVED);//(╯°□°)╯︵ ┻━┻
        player.quit(room.getGame());
        room.removeClient(clientHandler);
        broadCastMessage("211 " + clientHandler.getClientUsername() + " QUIT");//fixme sommmmmetimes it gives concurrent Exception??
        if(room.isEmpty()){
            Server.removeRoom(room);
        }
        clientHandler.purge();
        clientHandler.setGameState(new MenuState(clientHandler));
    }


    public void startGame(){
        if(room.isAdmin(clientHandler)){
            room.getGame().setCurrentPlayer(room.getGame().nextPlayer(0));
            rotateTurn();
        }
    }

    public void rotateTurn(){
        if(room.isEmpty()) {
            //todo return;
        }if(room.getGame().isRoundFinished()){
            broadCastMessageToEveryone("server : EndGame");
            //todo
            return;
        }else if(!room.turnIsUpToDate()){

            room.updateTurn();
            switch (room.getTurn()){
                case 0:
                    broadCastMessageToEveryone("server : Ante");
                    break;
                case 1:
                    broadCastMessageToEveryone("server : second betting round");
                    room.getGame().distributeCards(5);
                    notifyCardDistribution();
                    break;
                case 2:
                    broadCastMessageToEveryone("server : changing round");
                    break;
                case 3:
                    broadCastMessageToEveryone("server : third betting round");
                    break;
                default: broadCastMessageToEveryone("server : endgame");
            }

        }
        String currentPlayerName=room.getGame().getCurrentPlayer().getName();
        for (ClientHandler ch:room.getClientHandlers()){

            if (ch.getClientUsername().equals(currentPlayerName)){
                ch.addTask("(41[0123].*)|(710.*)");
                ch.writeToClient("Server : It is ur turn");
            }else {
                ch.writeToClient("Server : It is "+currentPlayerName+"'s turn");
            }

        }
    }



    public void notifyCardDistribution(){
        for(Player player : room.getGame().getPlayers()){
            Card[] cards= player.getCards();
            String cardDistribution="610 CARDS ";
            cardDistribution+=cards.length;
            for(Card card : cards) cardDistribution+=(" "+card.toString());
            ClientHandler ch=room.getClientHandler(player.getName());
            //(╯°□°)╯︵ ┻━┻
            ch.addTask(Request.CARDS_RECIEVED);
            ch.writeToClient(cardDistribution);
        }
    }

    public void leakCards(){
        int count=0;
        for(Player player : room.getGame().getPlayers()){
            Card[] cards= player.getCards();
            String cardDistribution="666 "+player.getName()+" "+count+" CARDS ";
            cardDistribution+=cards.length;
            for(Card card : cards) cardDistribution+=(" "+card.toString());
            writeToClient(cardDistribution);
            count+=1;
        }
    }
}
