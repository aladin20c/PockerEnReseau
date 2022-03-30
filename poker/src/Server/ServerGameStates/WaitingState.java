package Server.ServerGameStates;

import Game.Utils.Request;
import Server.ClientHandler;
import Server.SRoom;

import java.util.ArrayList;

public class WaitingState extends GameState {

    private String clientUsername;
    private SRoom currentRoom;
    private boolean startRequested;
    private int startRequestResponse;


    public WaitingState(ClientHandler clientHandler, String clientUsername, SRoom currentRoom) {
        super(clientHandler, 2);
        this.clientUsername=clientUsername;
        this.currentRoom = currentRoom;
        this.startRequested=false;
        this.startRequestResponse = -1;
    }

    @Override
    public void analyseRequest(String messageFromClient) {

        if(!messageFromClient.matches("\\d\\d\\d.+")) {

            broadCastMessageToEveryone(clientUsername+":"+messageFromClient,currentRoom.getClientHandlers());

        }else if (messageFromClient.matches(Request.ACK_Player)) {

            return;

        } else if (messageFromClient.matches(Request.START_ROUND)) {

            if (startRequested) {
                writeToClient("155 start already requested");
            } else if (!currentRoom.isAdmin(clientHandler)) {
                writeToClient("157 u are not the admin");
            } else if (!currentRoom.hasEnoughPlayersToStart()) {
                writeToClient("158 not enough players");
            } else {
                currentRoom.requestStart(true);
                this.startRequestResponse=1;
                broadCastMessage("152 START REQUESTED",currentRoom.getClientHandlers());
            }

        } else if (messageFromClient.matches(Request.START_RESPONSE)) {

            if (!startRequested) {
                writeToClient("158 there's no start request");
            } else if (this.startRequestResponse != -1) {
                writeToClient("158 u already responded to Start request");
            }else{
                String response = messageFromClient.substring(10);
                this.startRequestResponse = (response.equals("YES"))? 1:0;
                checkPlayersResponses();
            }


        } else if (messageFromClient.matches(Request.QUIT)) {

            clientQuit();

        } else if (messageFromClient.matches(Request.QUIT_RECIEVED)) {

            //nothing to do here(probably)

        }else{
            sendError();
        }
    }




    private void checkPlayersResponses(){

        ArrayList<String> playersWhoRefused=new ArrayList<>();
        for(ClientHandler ch : currentRoom.getClientHandlers()){
            switch (ch.getGameState().getResponse()){
                case -1: return;
                case 0: playersWhoRefused.add(ch.getClientUsername());break;
                case 1: break;
            }
        }

        if(playersWhoRefused.isEmpty()){
            broadCastMessageToEveryone("153 GAME STARTED",currentRoom.getClientHandlers());
            for(ClientHandler ch : currentRoom.getClientHandlers()){
                if(this.currentRoom.getType()==1) {
                    ch.setGameState(new PlayingTexasHoldemState(ch,ch.getClientUsername(),currentRoom));
                }else{
                    ch.setGameState(new Playing5CardPokerState(ch,ch.getClientUsername(),currentRoom));
                }
            }
            return;
        }else{
            int size = playersWhoRefused.size();
            broadCastMessageToEveryone("154 START ABORDED " + size,currentRoom.getClientHandlers());
            int i = 0;
            for (; i < playersWhoRefused.size() / 5; i++) {
                broadCastMessageToEveryone("154 MESS " + (i + 1) + " PLAYER "
                                + playersWhoRefused.get(i * 5) + " "
                                + playersWhoRefused.get(i * 5 + 1) + " "
                                + playersWhoRefused.get(i * 5 + 2) + " "
                                + playersWhoRefused.get(i * 5 + 3) + " "
                                + playersWhoRefused.get(i * 5 + 4)
                        ,currentRoom.getClientHandlers());
            }
            if (i * 5 < size) {
                broadCastMessageToEveryone("154 MESS " + (i + 1) + " PLAYER "
                                + playersWhoRefused.get(i * 5) + " "
                                + ((i * 5 + 1 < size) ? playersWhoRefused.get(i + 1) + " " : "")
                                + ((i * 5 + 2 < size) ? playersWhoRefused.get(i + 2) + " " : "")
                                + ((i * 5 + 3 < size) ? playersWhoRefused.get(i + 3) : "")
                        ,currentRoom.getClientHandlers());
            }
            currentRoom.requestStart(false);
        }
    }

    @Override
    public void clientQuit() {
        writeToClient(Request.QUIT_ACCEPTED);
        broadCastMessage("211 " + clientUsername + " QUIT",currentRoom.getClientHandlers());
        currentRoom.removeClient(clientHandler);
        if(startRequested){
            broadCastMessageToEveryone("153 GAME ABORDED " + 0,currentRoom.getClientHandlers());//todo maybe change this in the future
            requestStart(false);
        }
    }





    @Override
    public boolean canAddNewPlayer() {
        return !startRequested;
    }
    @Override
    public void requestStart(boolean start) {
        this.startRequestResponse=-1;
        this.startRequested=start;
    }
    @Override
    public int getResponse() {
        return startRequestResponse;
    }
}
