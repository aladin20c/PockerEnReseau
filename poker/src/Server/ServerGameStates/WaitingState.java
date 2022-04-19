package Server.ServerGameStates;

import Game.utils.Request;
import Server.ClientHandler;
import Server.Room;
import Server.Server;


import java.util.ArrayList;

public class WaitingState extends GameState {


    private boolean startRequested;
    private int startRequestResponse;


    public WaitingState(ClientHandler clientHandler, Room room) {
        super(clientHandler, room);
        this.startRequested=false;
        this.startRequestResponse = -1;
    }


    @Override
    public void analyseRequest(String messageFromClient) {

        if(!messageFromClient.matches("\\d\\d\\d.+")) {

            broadCastMessage(clientHandler.getClientUsername()+":"+messageFromClient);

        }else if (messageFromClient.matches(Request.ACK_Player)) {

            //(╯°□°)╯︵ ┻━┻
            clientHandler.cancelTask(messageFromClient);

        } else if (messageFromClient.matches(Request.START_ROUND)) {

            if (startRequested) {
                writeToClient("155 start already requested");
            } else if (!room.isAdmin(clientHandler)) {
                writeToClient("156 u are not the admin");
            } else if (!room.getGame().canStartGame()) {
                writeToClient("157 not enough players");
            } else {
                room.requestStart(true);
                this.startRequestResponse=1;
                //(╯°□°)╯︵ ┻━┻
                broadCastTask(Request.START_RESPONSE);
                broadCastMessage("152 START REQUESTED");
            }

        } else if (messageFromClient.matches(Request.START_RESPONSE)) {

            if (!startRequested) {
                writeToClient("158 there's no start request");
            } else if (this.startRequestResponse != -1) {
                writeToClient("159 u already responded to Start request");
            }else{
                String response = messageFromClient.substring(10);
                this.startRequestResponse = (response.equals("YES"))? 1:0;
                //(╯°□°)╯︵ ┻━┻
                clientHandler.cancelTask(messageFromClient);
                checkPlayersResponses();
            }

        } else if (messageFromClient.matches(Request.QUIT)) {

            clientQuit();

        } else if (messageFromClient.matches(Request.QUIT_RECIEVED)) {

            //(╯°□°)╯︵ ┻━┻
            clientHandler.cancelTask(messageFromClient);

        }else if(messageFromClient.matches(Request.GETSTATE)) {

            writeToClient("666 WaitingState");

        }else{

            clientHandler.writeToClient(Request.ERROR);

        }
    }




    private void checkPlayersResponses(){

        ArrayList<String> playersWhoRefused=new ArrayList<>();
        for(ClientHandler ch : room.getClientHandlers()){
            switch (ch.getGameState().getResponse()){
                case -1: return;
                case 0: playersWhoRefused.add(ch.getClientUsername());break;
                case 1: break;
            }
        }

        if(playersWhoRefused.isEmpty()){
            broadCastMessageToEveryone("153 GAME STARTED");
            for(ClientHandler ch : room.getClientHandlers()){
                if(room.getGame().getType()==1) {
                    ch.setGameState(new PlayingTexasHoldemState(ch,room));
                }else{
                    ch.setGameState(new Playing5CardPokerState(ch,room));
                }
            }
        }else{
            int size = playersWhoRefused.size();
            broadCastMessageToEveryone("154 START ABORDED " + size);
            int i = 0;
            for (; i < playersWhoRefused.size() / 5; i++) {
                broadCastMessageToEveryone("154 MESS " + (i + 1) + " PLAYER "
                                + playersWhoRefused.get(i * 5) + " "
                                + playersWhoRefused.get(i * 5 + 1) + " "
                                + playersWhoRefused.get(i * 5 + 2) + " "
                                + playersWhoRefused.get(i * 5 + 3) + " "
                                + playersWhoRefused.get(i * 5 + 4)
                        );
            }
            if (i * 5 < size) {
                broadCastMessageToEveryone("154 MESS " + (i + 1) + " PLAYER "
                                + playersWhoRefused.get(i * 5) + " "
                                + ((i * 5 + 1 < size) ? playersWhoRefused.get(i + 1) + " " : "")
                                + ((i * 5 + 2 < size) ? playersWhoRefused.get(i + 2) + " " : "")
                                + ((i * 5 + 3 < size) ? playersWhoRefused.get(i + 3) : "")
                        );
            }
            room.requestStart(false);
        }
    }

    @Override
    public void clientQuit() {
        room.removeClient(clientHandler);
        room.getGame().removePlayer(clientHandler.getClientUsername());
        writeToClient(Request.QUIT_ACCEPTED);
        //(╯°□°)╯︵ ┻━┻
        broadCastTask(Request.QUIT_RECIEVED);
        broadCastMessage("211 " + clientHandler.getClientUsername() + " QUIT");

        if(room.numberOfClients()==0) {
            Server.removeRoom(room);
        }else if(startRequested){
            broadCastMessageToEveryone("154 START ABORDED " + 0);
            //(╯°□°)╯︵ ┻━┻
            broadCastCancel(Request.START_RESPONSE);
            room.requestStart(false);
        }
        clientHandler.setGameState(new MenuState(clientHandler));
    }


    @Override
    public int getResponse() {
        return startRequestResponse;
    }
    public boolean startRequested() {return startRequested;}
    public void setStartRequested(boolean startRequested) {
        this.startRequested = startRequested;
    }
    public void setResponse(int startRequestResponse) {
        this.startRequestResponse = startRequestResponse;
    }
}
