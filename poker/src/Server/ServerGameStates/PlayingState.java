package Server.ServerGameStates;

import Game.Card;
import Game.Utils.Request;
import Server.ClientHandler;
import Server.SRoom;

import java.util.ArrayList;

public class PlayingState extends GameState{

    private String clientUsername;
    private SRoom currentRoom;

    private int stack;
    private int currentPlayerBids;
    private boolean hasFolded;
    private boolean allIn;
    private boolean dealer;

    private ArrayList<Card> cards;


    public PlayingState(ClientHandler clientHandler, String clientUsername, SRoom currentRoom) {
        super(clientHandler, 3);
        this.clientUsername=clientUsername;
        this.currentRoom=currentRoom;

        this.stack=currentRoom.getInitStack();
        this.currentPlayerBids=0;
        this.hasFolded=false;
        this.allIn=false;
        this.dealer=currentRoom.isAdmin(clientHandler);
    }


    @Override
    public void analyseRequest(String messageFromClient) {
        if(!messageFromClient.matches("\\d\\d\\d.+")) {

            broadCastMessageToEveryone(messageFromClient,currentRoom.getClientHandlers());

        }else if(messageFromClient.matches(Request.FOLD)){

            if(hasFolded){
                writeToClient("907 Invalid Command");
            }else {
                this.hasFolded = true;
                broadCastMessage("510 " + clientUsername + " FOLD",currentRoom.getClientHandlers());
                writeToClient("400 ACCEPTED");
            }

        }else if(messageFromClient.matches(Request.CHECK)){

            if(hasFolded || currentPlayerBids<currentRoom.getHighestBid()){
                writeToClient("907 Invalid Command");
            }else{
                writeToClient("400 ACCEPTED");
                broadCastMessage("511 "+ clientUsername +" CHECK",currentRoom.getClientHandlers());
            }

        }else if(messageFromClient.matches(Request.CALL)){

            if(hasFolded || currentPlayerBids>=currentRoom.getHighestBid() || this.stack<(currentRoom.getHighestBid()-currentPlayerBids) ) {
                writeToClient("907 Invalid Command");
            }else{
                this.stack=this.stack-(currentRoom.getHighestBid()-currentPlayerBids);
                this.currentPlayerBids = currentRoom.getHighestBid();
                writeToClient("400 ACCEPTED");
                broadCastMessage("512 "+ clientUsername +" CALL",currentRoom.getClientHandlers());
            }

        }else if(messageFromClient.matches(Request.RAISE)) {

            int raise = Integer.parseInt(messageFromClient.substring(10));
            if (hasFolded || currentPlayerBids > currentRoom.getHighestBid() || this.stack < (currentRoom.getHighestBid() + raise - currentPlayerBids)) {
                writeToClient("907 Invalid Command");
            }
            this.stack = this.stack - (currentRoom.getHighestBid() + raise - currentPlayerBids);
            currentRoom.incrementHighestBid(raise);
            currentPlayerBids = currentRoom.getHighestBid();
            writeToClient("400 ACCEPTED");
            broadCastMessage("513 " + clientUsername + " RAISE " + raise,currentRoom.getClientHandlers());

        }else if(messageFromClient.matches(Request.ACTION_RECIEVED)){

            //Nothing here (probably)

        }else if(messageFromClient.matches(Request.CARDS_RECIEVED)){

            //Nothing here (probably)

        }else if(messageFromClient.matches(Request.CHANGE)){

            if(currentRoom.getType()==1 || cards==null){
                writeToClient("999 ERROR");
                return;
            }
            String[] data=messageFromClient.split("\\s+");
            int numberOfCards=Integer.parseInt(data[2]);
            if(data.length!=numberOfCards+3){
                writeToClient("999 ERROR");
                return;
            }
            //TODO
            writeToClient("700 ACCEPTED");
            broadCastMessage("720 "+clientUsername+" Change "+numberOfCards,currentRoom.getClientHandlers());

        }else if(messageFromClient.matches(Request.CHANGE_RECIEVED)){

            //Nothing here (probably)

        }else if (messageFromClient.matches(Request.QUIT)) {

            playerQuit();

        } else if (messageFromClient.matches(Request.QUIT_RECIEVED)) {

            //nothing to do here(probably)

        }else{
            sendError();
        }
    }


    @Override
    public void playerQuit() {
        writeToClient(Request.QUIT_ACCEPTED);
        broadCastMessage("211 " + clientUsername + " QUIT",currentRoom.getClientHandlers());
        currentRoom.removeClient(clientHandler);
        //TODO further actions
    }
}
