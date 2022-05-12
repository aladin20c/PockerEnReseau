package Server.ServerGameStates;

import Game.Player;
import Game.utils.Request;
import Server.ClientHandler;
import Server.Room;
import Server.Server;


import java.io.IOException;
import java.util.ArrayList;

public class WaitingState extends GameState {


    private int startRequestResponse;


    public WaitingState(ClientHandler clientHandler, Room room) {
        super(clientHandler, room);
        this.startRequestResponse = 0;
    }


    @Override
    public void analyseRequest(String messageFromClient) {

        if(!messageFromClient.matches("\\d\\d\\d.+")) {

            broadCastMessage(clientHandler.getClientUsername()+":"+messageFromClient);

        }else if (messageFromClient.matches(Request.ACK_Player)) {

            clientHandler.cancelTask(messageFromClient);//(╯°□°)╯︵ ┻━┻

        } else if (messageFromClient.matches(Request.START_ROUND)) {

            if (room.isStartRequested()) {
                writeToClient("155 start already requested");
            } else if (!room.isAdmin(clientHandler)) {
                writeToClient("156 u are not the admin");
            } else if (!room.getGame().canStartGame()) {
                writeToClient("157 not enough players");
            } else {
                room.setStartRequested(true);
                this.startRequestResponse=1;
                broadCastTask(Request.START_RESPONSE);//(╯°□°)╯︵ ┻━┻
                broadCastMessage("152 START REQUESTED");
            }

        } else if (messageFromClient.matches(Request.START_RESPONSE)) {

            if (!room.isStartRequested()) {
                writeToClient("158 there's no start request");
            } else if (this.startRequestResponse != 0) {
                writeToClient("159 u already responded to Start request");
            }else{
                String response = messageFromClient.substring(10);
                this.startRequestResponse = (response.equals("YES"))? 1:-1;
                clientHandler.cancelTask(messageFromClient);//(╯°□°)╯︵ ┻━┻
                checkPlayersResponses();
            }

        } else if (messageFromClient.matches(Request.QUIT)) {

            clientQuit();

        } else if (messageFromClient.matches(Request.QUIT_RECIEVED)) {

            //(╯°□°)╯︵ ┻━┻
            clientHandler.cancelTask(messageFromClient);

        }else if(messageFromClient.matches(Request.GET_STATE)) {

            writeToClient("666 WaitingState");

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

        }else{

            clientHandler.writeToClient(Request.ERROR);

        }
    }




    private void checkPlayersResponses(){

        ArrayList<String> playersWhoRefused=new ArrayList<>();
        for(ClientHandler ch : room.getClientHandlers()){
            switch (ch.getGameState().getStartResponse()){
                case 0: return;
                case -1: playersWhoRefused.add(ch.getClientUsername());break;
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
            room.setStartRequested(false);
        }
    }

    @Override
    public void clientQuit() {
        room.removeClient(clientHandler);
        room.getGame().removePlayer(clientHandler.getClientUsername());
        broadCastTask(Request.QUIT_RECIEVED);//(╯°□°)╯︵ ┻━┻
        broadCastMessage("211 " + clientHandler.getClientUsername() + " QUIT");
        if(room.numberOfClients()==0) {
            Server.removeRoom(room);
        }else if(room.isStartRequested()){
            broadCastCancel(Request.START_RESPONSE);//(╯°□°)╯︵ ┻━┻
            broadCastMessageToEveryone("154 START ABORDED " + 0);
            room.setStartRequested(false);
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


    @Override
    public int getStartResponse() {
        return startRequestResponse;
    }
    public void setStartResponse(int startRequestResponse) {
        this.startRequestResponse = startRequestResponse;
    }
}
