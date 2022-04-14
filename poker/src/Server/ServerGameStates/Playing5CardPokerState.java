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
                writeToClient("907 Invalid Command");
            }else {
                player.fold(room.getGame());
                broadCastMessage("510 " + clientHandler.getClientUsername() + " FOLD");
                writeToClient("400 ACCEPTED");
                rotateTurn();
            }

        }else if(messageFromClient.matches(Request.CHECK)){

            if(!room.getGame().canCheck(player)){
                writeToClient("907 Invalid Command");
            }else{
                player.check(room.getGame());
                writeToClient("400 ACCEPTED");
                broadCastMessage("511 "+ clientHandler.getClientUsername() +" CHECK");
                rotateTurn();
            }

        }else if(messageFromClient.matches(Request.CALL)){

            if(!room.getGame().canCall(player)){
                writeToClient("907 Invalid Command");
            }else{
                player.call(room.getGame());
                writeToClient("400 ACCEPTED");
                broadCastMessage("512 "+ clientHandler.getClientUsername() +" CALL");
                rotateTurn();
            }

        }else if(messageFromClient.matches(Request.RAISE)) {

            int raise = Integer.parseInt(messageFromClient.substring(10));
            if(!room.getGame().canRaise(player,raise)){
                writeToClient("907 Invalid Command");
            }else {
                player.raise(room.getGame(), raise);
                writeToClient("400 ACCEPTED");
                broadCastMessage("513 " + clientHandler.getClientUsername() + " RAISE " + raise);
                rotateTurn();
            }

        }else if(messageFromClient.matches(Request.ACTION_RECIEVED)){

            //Nothing here (probably)

        }else if(messageFromClient.matches(Request.CARDS_RECIEVED)){

            //Nothing here (probably)

        }else if(messageFromClient.matches(Request.CHANGE)){

            String[] data=messageFromClient.split("\\s+");
            int numberOfCards=Integer.parseInt(data[2]);
            Card[] cards=new Card[numberOfCards];
            for(int i=3;i<data.length;i++){
                cards[i-3]=new Card(data[i]);
            }

            if(data.length!=numberOfCards+3 || !room.getGame().canChange(player,cards)){
                writeToClient("999 ERROR");
            }else{
                Card[] newCards=room.getGame().change(player,cards);
                writeToClient("700 ACCEPTED");
                broadCastMessage("720 "+clientHandler.getClientUsername()+" Change "+numberOfCards);
                String cardDistribution="610 CARDS ";
                cardDistribution+=(data[2]);
                for(Card card:newCards){
                    cardDistribution=cardDistribution+" "+card.toString();
                }
                rotateTurn();
            }


        }else if(messageFromClient.matches(Request.CHANGE_RECIEVED)){

            //Nothing here (probably)

        }else if (messageFromClient.matches(Request.QUIT)) {

            clientQuit();
            rotateTurn();

        } else if (messageFromClient.matches(Request.QUIT_RECIEVED)) {

            //nothing to do here(probably)

        }else{
            clientHandler.writeToClient(Request.ERROR);
        }
    }


    @Override
    public void clientQuit() {
        writeToClient(Request.QUIT_ACCEPTED);
        broadCastMessage("211 " + clientHandler.getClientUsername() + " QUIT");
        player.quit(room.getGame());
        room.removeClient(clientHandler);
        if(room.numberOfClients()==0){
            Server.removeRoom(room);
        }
        clientHandler.setGameState(new MenuState(clientHandler));
        //TODO further actions
    }


    public void startGame(){
        if(room.isAdmin(clientHandler)){
            room.getGame().setCurrentPlayer(room.getGame().nextPlayer(0));
            rotateTurn();
        }
    }

    public void rotateTurn(){
        if(room.getGame().isRoundFinished()){
            broadCastMessageToEveryone("server : EndGame");
            //todo
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
            room.getClientHandler(player.getName()).writeToClient(cardDistribution);
        }
    }
}
