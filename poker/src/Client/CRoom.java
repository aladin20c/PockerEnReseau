package Client;

import Game.Room;

import java.util.ArrayList;

public class CRoom extends Room {
    ArrayList<PlayerInformations> players;
    public CRoom(int type, int minPlayers, int minBid, int initStack) {
        super(type, minPlayers, minBid, initStack);
        this.players=new ArrayList<>();
    }




    public boolean hasRoomLeft(){return players.size()<this.getMinPlayers();}
    public int numberOfPlayers(){return players.size();}
    public boolean isAdmin(String userName){
        if(players.isEmpty()) return false;
        return players.get(0).getUserName().equals(userName);
    };
}
