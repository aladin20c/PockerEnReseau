package Client;

import Game.Room;

import java.util.ArrayList;

public class CRoom extends Room {
    ArrayList<Player> players;

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
        for(Player p : players){
            if(p.userName.equals(name)){
                p.hasFolded=true;
                return;
            }
        }
    }
    public void addPlayer(String userName) {this.players.add(new Player(userName,getInitStack()));}
    public boolean hasRoomLeft(){return players.size()<this.getMaxPlayers();}
    public int numberOfPlayers(){return players.size();}
    public boolean isAdmin(String userName){
        if(players.isEmpty()) return false;
        return players.get(0).getUserName().equals(userName);
    }
    public Player getPlayer(String username){
        for(Player player : players){
            if(player.userName.equals(username)) return player;
        }
        return null;
    }
    public boolean hasEnoughPlayersToStart(){
        return (getType()==0 && players.size()>=3) || (getType()==1 && players.size()>=2);
    }
}
