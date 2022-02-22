package Client;

import Game.Room;

import java.util.ArrayList;

public class CRoom extends Room {
    ArrayList<PlayerInformations> players;

    public CRoom(int id,int type, int minPlayers, int minBid, int initStack) {
        super(id,type, minPlayers, minBid, initStack);
        this.players=new ArrayList<>();
    }
    public CRoom(int type, int minPlayers, int minBid, int initStack) {
        //creating a room without incremanting id
        super(0,type, minPlayers, minBid, initStack);
        this.players=new ArrayList<>();
    }



    public void playerQuit(String name){
        for(PlayerInformations p : players){
            if(p.userName.equals(name)){
                p.hasFolded=true;
                return;
            }
        }
    }
    public void addPlayer(String userName) {this.players.add(new PlayerInformations(userName,getInitStack()));}
    public boolean hasRoomLeft(){return players.size()<this.getMinPlayers();}
    public int numberOfPlayers(){return players.size();}
    public boolean isAdmin(String userName){
        if(players.isEmpty()) return false;
        return players.get(0).getUserName().equals(userName);
    }
    public PlayerInformations getPlayer(String username){
        for(PlayerInformations player : players){
            if(player.userName.equals(username)) return player;
        }
        return null;
    }
}
