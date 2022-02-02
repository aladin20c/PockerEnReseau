package Server;

import java.util.ArrayList;

public class Room {
    private static int NUMBER_OF_ROOMS=0;
    private ArrayList<ClientHandler> clientHandlers=new ArrayList<>();

    private int maxNumberOfPlayers;
    private int minimalBet;
    private int initialStack;
    private int id;

    public Room(int maxNumberOfPlayers, int minimalBet, int initialStack) {
        this.maxNumberOfPlayers = maxNumberOfPlayers;
        this.minimalBet = minimalBet;
        this.initialStack = initialStack;
        this.id=++NUMBER_OF_ROOMS;
    }

    public ArrayList<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }


}
