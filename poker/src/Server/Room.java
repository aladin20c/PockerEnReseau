package Server;

import java.util.ArrayList;

public class Room {

    private ArrayList<ClientHandler> clientHandlers;

    public Room(int type, int numberOfPlayers,int bet,int stack) {
        this.clientHandlers = new ArrayList<>();
    }

}
