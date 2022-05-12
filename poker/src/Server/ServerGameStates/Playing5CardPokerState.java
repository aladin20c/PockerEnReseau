package Server.ServerGameStates;

import Game.Card;
import Game.Player;
import Game.utils.Request;
import Server.ClientHandler;
import Server.Server;
import Server.Room;

import java.io.IOException;


public class Playing5CardPokerState extends GameState{

    private final Player player;
    private int endgameResponse;


    public Playing5CardPokerState(ClientHandler clientHandler , Room room) {
        super(clientHandler, room);
        this.player=room.getGame().getPlayer(clientHandler.getClientUsername());
        this.endgameResponse=0;
        startGame();
    }

    public void startGame(){
        if(room.isAdmin(clientHandler)){
            room.getGame().setCurrentPlayer(room.getGame().nextPlayer(0));
            rotateTurn();
        }
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
                player.raise(room.getGame(), raise);
                clientHandler.cancelTask(messageFromClient);//(╯°□°)╯︵ ┻━┻
                broadCastTask(Request.ACTION_RECIEVED);//(╯°□°)╯︵ ┻━┻
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
                try {
                    cards[i-3]=new Card(data[i]);
                }catch (RuntimeException e){
                    writeToClient(Request.ERROR);
                }
            }
            if(data.length!=numberOfCards+3 || !room.getGame().canChange(player,cards)){
                writeToClient(Request.ERROR);
            }else{
                Card[] newCards=room.getGame().change(player,cards);
                clientHandler.cancelTask(messageFromClient);//(╯°□°)╯︵ ┻━┻
                writeToClient("700 ACCEPTED");
                broadCastTask(Request.CHANGE_RECIEVED);//(╯°□°)╯︵ ┻━┻
                broadCastMessage("720 "+clientHandler.getClientUsername()+" CHANGE "+numberOfCards);
                StringBuilder cardDistribution= new StringBuilder("610 CARDS ");
                cardDistribution.append(data[2]);
                for(Card card:newCards){
                    cardDistribution.append(" ").append(card.toString());
                }
                clientHandler.addTask(Request.CARDS_RECIEVED);//(╯°□°)╯︵ ┻━┻
                writeToClient(cardDistribution.toString());
                rotateTurn();
            }

        }else if(messageFromClient.matches(Request.CHANGE_RECIEVED)){

            clientHandler.cancelTask(messageFromClient);//(╯°□°)╯︵ ┻━┻

        }else if (messageFromClient.matches(Request.QUIT)) {

            clientQuit();

        }else if (messageFromClient.matches(Request.QUIT_RECIEVED)) {

            clientHandler.cancelTask(messageFromClient);//(╯°□°)╯︵ ┻━┻

        }else if(messageFromClient.matches(Request.WINRECEIVED)) {

            if(room.isEndgame()){
                this.endgameResponse=1;
                room.setResetGameTimer();
            }else {
                writeToClient(Request.ERROR);
            }

        }else if(messageFromClient.matches(Request.GET_STATE)) {

            writeToClient("666 PlayingTexasHoldemState");

        }else if(messageFromClient.matches(Request.GET_ALL_CARDS)) {

            leakCards();

        }else if(messageFromClient.matches(Request.GET_ALL_PLAYERS)) {

            StringBuilder playerList= new StringBuilder("666 " + room.getGame().getPlayers().size() + " ALLPLAYERS");
            for(Player player : room.getGame().getPlayers()){
                playerList.append(" ").append(player.getName());
            }
            writeToClient(playerList.toString());

        }else if(messageFromClient.matches(Request.GET_ACTIVE_PLAYERS)) {

            StringBuilder playerList= new StringBuilder("666 " + room.getGame().getPlayers().size() + " ACTIVEPLAYERS");
            for(ClientHandler ch : room.getClientHandlers()){
                playerList.append(" ").append(ch.getClientUsername());
            }
            writeToClient(playerList.toString());

        }else if(messageFromClient.matches(Request.GET_QUITTED_PLAYERS)) {

            StringBuilder playerList= new StringBuilder("666 " + room.getGame().getPlayers().size() + " QUITTEDPLAYERS");
            for(Player player : room.getGame().getPlayers()){
                if(player.hasQuitted())playerList.append(" ").append(player.getName());
            }
            writeToClient(playerList.toString());

        }else if(messageFromClient.matches(Request.GET_FOLDED_PLAYERS)) {

            StringBuilder playerList= new StringBuilder("666 " + room.getGame().getPlayers().size() + " FOLDEDPLAYERS");
            for(Player player : room.getGame().getPlayers()){
                if(player.hasFolded())playerList.append(" ").append(player.getName());
            }
            writeToClient(playerList.toString());

        }else{

            clientHandler.writeToClient(Request.ERROR);

        }
    }


    @Override
    public void clientQuit() {
        boolean current=room.getGame().getCurrentPlayer().getName().equals(clientHandler.getClientUsername());
        player.quit(room.getGame());
        room.removeClient(clientHandler);
        broadCastTask(Request.QUIT_RECIEVED);//(╯°□°)╯︵ ┻━┻
        broadCastMessage("211 " + clientHandler.getClientUsername() + " QUIT");
        if(current){
            rotateTurn();
        }
        if(room.isEmpty()){
            Server.removeRoom(room);
        }

        try{
            clientHandler.purge();
            clientHandler.setGameState(new MenuState(clientHandler));
            clientHandler.getBufferedWriter().write(Request.QUIT_ACCEPTED);
            clientHandler.getBufferedWriter().newLine();
            clientHandler.getBufferedWriter().flush();
        }catch (IOException e){
            //there must no call for close everything
            //recursive
        }

    }


    public void rotateTurn(){
        if(room.isEndgame()) {
            return;
        }else if(room.getGame().isRoundFinished()){
            broadCastMessageToEveryone("server : EndGame");
            room.setEndgame(true);
            declareWin();
            return;
        }else if(!room.turnIsUpToDate()) {

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
                default:
                    broadCastMessageToEveryone("server : endgame");
                    room.setEndgame(true);
                    declareWin();
                    return;
            }

        }
        String currentPlayerName=room.getGame().getCurrentPlayer().getName();
        for (ClientHandler ch:room.getClientHandlers()){

            if (ch.getClientUsername().equals(currentPlayerName)){
                //todo fixme _____
                if(room.getTurn()==2){
                    ch.addTask(Request.CHANGE);
                }else{
                    ch.addTask("41[0123].*");
                }
                ch.writeToClient("Server : It is ur turn");
            }else {
                ch.writeToClient("Server : It is "+currentPlayerName+"'s turn");
            }

        }
    }


    public void notifyCardDistribution(){
        for(Player player : room.getGame().getPlayers()){
            Card[] cards= player.getCards();
            StringBuilder cardDistribution= new StringBuilder("610 CARDS ");
            cardDistribution.append(cards.length);
            for(Card card : cards) cardDistribution.append(" ").append(card.toString());
            ClientHandler ch=room.getClientHandler(player.getName());
            ch.addTask(Request.CARDS_RECIEVED);//(╯°□°)╯︵ ┻━┻
            ch.writeToClient(cardDistribution.toString());
        }
    }

    public void leakCards(){
        int count=0;
        for(Player player : room.getGame().getPlayers()){
            Card[] cards= player.getCards();
            StringBuilder cardDistribution= new StringBuilder("666 " + player.getName() + " " + count + " CARDS ");
            cardDistribution.append(cards.length);
            for(Card card : cards) cardDistribution.append(" ").append(card.toString());
            writeToClient(cardDistribution.toString());
            count+=1;
        }
    }

    public void declareWin(){
        if(room.isEmpty()) return;
        int count=room.getGame().getPlayers().size()-room.getGame().getFoldedPlayers();
        broadCastMessageToEveryone("810 REVEALCARD "+count);
        room.getGame().setWinners();
        StringBuilder winners= new StringBuilder("810 ");
        for (Player player : room.getGame().getWinners()){
            winners.append(player.getName()).append(" ");
        }
        winners.append("WIN");
        broadCastMessageToEveryone(winners.toString());
        int i=1;
        for (Player player : room.getGame().getPlayers()){
            if(!player.hasFolded()) {
                StringBuilder playerinfo= new StringBuilder("810 ");
                playerinfo.append(player.getName()).append(" ").append(i).append(" HAS");
                for (Card card : player.getCards()) playerinfo.append(" ").append(card.toString());
                broadCastMessageToEveryone(playerinfo.toString());
                i+=1;
            }
        }
        room.purgeTasks();
    }

    @Override
    public int getEndgameResponse() {
        return endgameResponse;
    }
    @Override
    public void setEndgameResponse(int endgameResponse) {
        this.endgameResponse = endgameResponse;
    }




}
