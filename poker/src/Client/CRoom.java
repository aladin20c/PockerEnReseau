package Client;

import Game.Room;

import java.util.ArrayList;

public class CRoom extends Room {
    ArrayList<Player> players;


    public CRoom(int id,int type, int minPlayers, int minBid, int initStack) {
        super(id,type, minPlayers, minBid, initStack);
        this.players=new ArrayList<>();
    }

    public void addPlayer(String userName) {
        this.players.add(new Player(userName,getInitStack()));
    }
    public void removePlayer(String userName) {
        for(Player player : players){
            if(player.userName.equals(userName)) {
                players.remove(player);
                return;
            }
        }
    }

    public Player getPlayer(String username){
        for(Player player : players){
            if(player.userName.equals(username)) return player;
        }
        return null;
    }

    public void switchFirsttoLast(){
        if(players!=null && !players.isEmpty()){
            players.add(players.remove(0));
        }
    }


    public void add(Player player){
        players.add(player);
    }
    public Player remove(){
        return players.remove(players.size()-1);
    }
}
