package Server.ServerGameStates;

import Client.Player;
import Game.Card;
import Game.Utils.Request;
import Server.ClientHandler;
import Server.SRoom;
import Server.Server;


public class Playing5CardPokerState extends GameState{

    private String clientUsername;
    private SRoom currentRoom;
    private Player player;


    public Playing5CardPokerState(ClientHandler clientHandler, String clientUsername, SRoom currentRoom) {
        super(clientHandler, 3);
        this.clientUsername=clientUsername;
        this.currentRoom=currentRoom;
        this.player=currentRoom.getPlayer(clientUsername);
        this.player.getHand();
        startGame();
    }


    @Override
    public void analyseRequest(String messageFromClient) {
        if(!messageFromClient.matches("\\d\\d\\d.+")) {

            broadCastMessageToEveryone(clientUsername+":"+messageFromClient,currentRoom.getClientHandlers());

        }else if(messageFromClient.matches(Request.FOLD)){

            if(!player.canFold()){
                writeToClient("907 Invalid Command");
            }else {
                player.fold();
                broadCastMessage("510 " + clientUsername + " FOLD",currentRoom.getClientHandlers());
                writeToClient("400 ACCEPTED");
                rotateTurn();
            }

        }else if(messageFromClient.matches(Request.CHECK)){

            if(!player.canCheck(currentRoom)){
                writeToClient("907 Invalid Command");
            }else{
                player.check();
                writeToClient("400 ACCEPTED");
                broadCastMessage("511 "+ clientUsername +" CHECK",currentRoom.getClientHandlers());
                rotateTurn();
            }

        }else if(messageFromClient.matches(Request.CALL)){

            if(!player.canCall(currentRoom)){
                writeToClient("907 Invalid Command");
            }else{
                player.call(currentRoom);
                writeToClient("400 ACCEPTED");
                broadCastMessage("512 "+ clientUsername +" CALL",currentRoom.getClientHandlers());
                rotateTurn();
            }

        }else if(messageFromClient.matches(Request.RAISE)) {

            int raise = Integer.parseInt(messageFromClient.substring(10));
            if(!player.canRaise(currentRoom,raise)){
                writeToClient("907 Invalid Command");
            }
            player.raise(currentRoom,raise);
            writeToClient("400 ACCEPTED");
            broadCastMessage("513 " + clientUsername + " RAISE " + raise,currentRoom.getClientHandlers());
            rotateTurn();

        }else if(messageFromClient.matches(Request.ACTION_RECIEVED)){

            //Nothing here (probably)

        }else if(messageFromClient.matches(Request.CARDS_RECIEVED)){

            //Nothing here (probably)

        }else if(messageFromClient.matches(Request.CHANGE)){

            String[] data=messageFromClient.split("\\s+");
            int numberOfCards=Integer.parseInt(data[2]);
            if(!player.canChange(numberOfCards) || data.length!=numberOfCards+3){
                writeToClient("999 ERROR");
                return;
            }
            Card[] cards=new Card[numberOfCards];
            for(int i=3;i<data.length;i++){
                cards[i-3]=new Card(data[i]);
            }


            //TODO player.discard(deck,cards)
            //TODO currentRoom.deal(player,numberOfCards)


            writeToClient("700 ACCEPTED");
            broadCastMessage("720 "+clientUsername+" Change "+numberOfCards,currentRoom.getClientHandlers());


            rotateTurn();




        }else if(messageFromClient.matches(Request.CHANGE_RECIEVED)){

            //Nothing here (probably)

        }else if (messageFromClient.matches(Request.QUIT)) {

            clientQuit();

        } else if (messageFromClient.matches(Request.QUIT_RECIEVED)) {

            //nothing to do here(probably)

        }else{
            sendError();
        }
    }


    public void startGame(){
        currentRoom.startGame();
        writeToClient("ante Round");
    }
    public void resetGame(){
        currentRoom.resetGame();
    }
    public void rotateTurn(){
        currentRoom.currentPlayer.setPlayedInThisTurn(true);
        currentRoom.setCurrentPlayer(currentRoom.nextPlayer());

        for (Player player : currentRoom.players){
            if(!player.hasFolded && (!player.playedInThisTurn || player.bids!=currentRoom.highestBid)) return;
        }

        //passing to next round
        currentRoom.round+=1;
        currentRoom.setAllPlayersDidntPlay();
        currentRoom. setCurrentPlayer(currentRoom.playerleftToDealer());

        switch (currentRoom.round){
            case 1: broadCastMessageToEveryone("Srever:distributing cards",currentRoom.getClientHandlers());
                    currentRoom.round+=1;
            case 2: broadCastMessageToEveryone("Srever:first betting Round",currentRoom.getClientHandlers());
                    //and ditribute cards
                    break;
            case 3: broadCastMessageToEveryone("Srever:changing cards Round",currentRoom.getClientHandlers());break;
            case 4: broadCastMessageToEveryone("Srever:second betting Round",currentRoom.getClientHandlers());break;
            case 5: broadCastMessageToEveryone("Srever:final Round",currentRoom.getClientHandlers());
                    //revealcards();
                    break;
        }

    }



    /*private Set<Action> getAllowedActions(Player player, SRoom room) {
        Set<Action> actions = new HashSet<>();
        if(room.round<5) {
            actions.add(Action.FOLD);
            if (player.bids < currentRoom.getMinBid()) {
                actions.add(Action.BIG_BLIND);
            } else if (room.round == 3) {
                //actions.add(Action.);
            } else {
                if (player.canCheck(room)) actions.add(Action.CHECK);
                if (player.canCall(room)) {
                    actions.add(Action.CALL);
                    actions.add(Action.RAISE);
                }
            }
        }
        return actions;
    }*/





    @Override
    public void clientQuit() {
        writeToClient(Request.QUIT_ACCEPTED);
        broadCastMessage("211 " + clientUsername + " QUIT",currentRoom.getClientHandlers());
        player.setInactive();
        if(currentRoom.isCurrentPlayer(clientUsername)){
            rotateTurn();
        }
        currentRoom.removeClient(clientHandler);
        if(currentRoom.numberOfClients()==0){
            Server.removeRoom(currentRoom);
        }
        //TODO further actions
    }
}
