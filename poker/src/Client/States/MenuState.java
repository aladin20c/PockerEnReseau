package Client.States;

import Client.CRoom;
import Client.Client;
import Game.Utils.Request;

public class MenuState extends GameState{

    private String username;
    private CRoom futureRoom;
    private CRoom[] roomsList;

    public MenuState(Client client, String username) {
        super(client, 1);
        this.username = username;
        System.out.println("[gameState][menuState] entering....");
    }


    @Override
    public void analyseMessageToSend(String messageToSend) {
        if (messageToSend.matches(Request.CREATE_ROOM)) {
            String[] words = messageToSend.substring(11).split("\\s*[a-zA-Z]+\\s+");

            int type = Integer.parseInt(words[0]);
            int numberOfPlayers = Integer.parseInt(words[1]);
            int minBet = Integer.parseInt(words[2]);
            int initialStack = Integer.parseInt(words[3]);
            //preparing the room that will be created by the user and that he ll join
            if (futureRoom == null) futureRoom = new CRoom(0,type, numberOfPlayers, minBet, initialStack);
        }
    }

    @Override
    public void analyseComingMessage(String comingMessage) {
        if (comingMessage.matches(Request.GAME_CREATED)) {

            int gameId = Integer.parseInt(comingMessage.substring(17));
            if (futureRoom==null) throw new RuntimeException("player is not attempting to create a room");
            futureRoom.setId(gameId);
            this.client.setGameState(new WaitingState(client,username,futureRoom));

        }else if (comingMessage.matches(Request.LIST_LENGTH)) {

            int length = Integer.parseInt(comingMessage.substring(11));
            if (length != 0) this.roomsList = new CRoom[length];

        } else if (comingMessage.matches(Request.ROOM_INFOS)) {

            int index = Integer.parseInt(comingMessage.substring(9, comingMessage.indexOf(" ID")));
            String Infos = comingMessage.substring(comingMessage.indexOf("ID") + 2);
            String[] attributes = Infos.split("\\s+");
            int id = Integer.parseInt(attributes[1]);
            int type = Integer.parseInt(attributes[2]);
            int numberOfPlayers = Integer.parseInt(attributes[3]);
            int minBet = Integer.parseInt(attributes[4]);
            int initialStack = Integer.parseInt(attributes[5]);
            int existingPlayers = Integer.parseInt(attributes[6]);
            this.roomsList[index - 1] = new CRoom(id, type, numberOfPlayers, minBet, initialStack);

        } else if (comingMessage.matches(Request.ROOM_JOINED)) {

            int id = Integer.parseInt(comingMessage.substring(9, comingMessage.length() - 7));
            if (this.roomsList==null) throw new RuntimeException("player is not attempting to join a room");
            for (CRoom room : roomsList) {
                if (room.getId() == id) {
                    this.client.setGameState(new WaitingState(client,username,room));
                    return;
                }
            }
            throw new RuntimeException("room not found");
        }
    }
}
