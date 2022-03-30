package Client.States;

import Client.Player;
import Client.Client;
import Game.Room;
import Game.Utils.Request;


public class WaitingState extends GameState{

    private String username;
    private Room currentRoom;
    private boolean startRequested;


    public WaitingState(Client client, String username, Room currentRoom) {
        super(client, 2);
        this.username = username;
        this.currentRoom = currentRoom;
        this.startRequested=false;
        System.out.println("[Client][gameState][WaitingState] waiting for game start....");
    }


    @Override
    public void analyseMessageToSend(String messageToSend) {
        if(startRequested && !messageToSend.matches(Request.START_RESPONSE)){
            System.out.println("u must respond to start request first !!!!!!");
        }
    }

    @Override
    public void analyseComingMessage(String comingMessage) {

        if (comingMessage.matches(Request.PLAYERS_LIST)) {

            currentRoom.addPlayer(username);

        } else if (comingMessage.matches(Request.PLAYERS_INFOS)) {

            String message=comingMessage.substring(comingMessage.indexOf("PLAYER"));
            String[] data=message.split("\\s+");

            Player tmp=currentRoom.players.remove(currentRoom.players.size()-1);

            for(int i=1;i< data.length;i++){
                currentRoom.addPlayer(data[i]);
            }
            currentRoom.players.add(tmp);


        }else if (comingMessage.matches(Request.PLAYER_JOINED)) {

            String name = comingMessage.substring(4, comingMessage.length() - 7);
            currentRoom.addPlayer(name);
            writeToServer("141 " + name + " ACK");

        } else if (comingMessage.matches(Request.START_IS_REQUESTED)) {

            this.startRequested=true;

        } else if (comingMessage.matches(Request.GAME_STARTED)) {

            if(this.currentRoom.getType()==1) {
                this.client.setGameState(new PlayingTexasHoldemState(client, username, currentRoom));
            }else{
                this.client.setGameState(new Playing5CardPokerState(client, username, currentRoom));
            }

        } else if (comingMessage.matches(Request.GAME_ABORDED)) {

            this.startRequested=false;

        }else if (comingMessage.matches(Request.QUIT_ACCEPTED)) {

            quit();

        } else if (comingMessage.matches(Request.PLAYER_QUIT)) {

            String name = comingMessage.substring(4, comingMessage.length()-5);
            currentRoom.removePlayer(name);
            writeToServer(Request.QUIT_RECIEVED);

        }
    }

    @Override
    public void quit() {
        this.client.setGameState(new MenuState(client,username));
    }
}
