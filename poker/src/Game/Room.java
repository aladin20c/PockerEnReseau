package Game;

import Client.Player;

import java.util.ArrayList;

public class Room {


    private int id;
    private int type;
    private int maxPlayers;
    private int minBid;
    private int initStack;


    public ArrayList<Player> players;
    public Player dealer;
    public Player currentPlayer;
    public int pot;
    public int highestBid;
    public int round;
    public Hand table;
    public Deck deck;


    public Room(int id, int type, int maxPlayers, int minBid, int initStack) {
        this.id = id;
        this.type = type;
        this.maxPlayers = maxPlayers;
        this.minBid = minBid;
        this.initStack = initStack;
        this.highestBid=0;
        this.pot=0;
        this.round=0;
        this.players=new ArrayList<>();
        this.deck=new Deck();
    }

    public void startGame(){
        setDealer(players.get(0));
        setCurrentPlayer(this.playerleftToDealer());
        round=0;
        this.setAllPlayersDidntPlay();
    }
    public void resetGame(){
        setDealer(this.playerleftToDealer());
        setCurrentPlayer(this.playerleftToDealer());
        round=0;
        this.setAllPlayersDidntPlay();
        this.setAllPlayersDidntFold();
    }

    public Player playerleftToDealer(){
        int index=players.indexOf(dealer);
        int numberOfPlayers=players.size();

        for(int j=0;j<numberOfPlayers;j++){
            if(players.get(index)!=dealer && !players.get(index).hasQuitted && !players.get(index).hasFolded) {
                return players.get(index);
            }
            index=(index+1)%numberOfPlayers;
        }
        throw new RuntimeException("dealer is alone");
    }
    public Player nextPlayer(){
        int index=players.indexOf(currentPlayer);
        int numberOfPlayers=players.size();

        for(int j=0;j<numberOfPlayers;j++){
            if(players.get(index)!=currentPlayer && !players.get(index).hasQuitted && !players.get(index).hasFolded) {
                return players.get(index);
            }
            index=(index+1)%numberOfPlayers;
        }
        throw new RuntimeException("current player is alone");
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

    public boolean isCurrentPlayer(Player player){
        return currentPlayer==player;
    }
    public boolean isCurrentPlayer(String username){
        return currentPlayer.userName.equals(username);
    }


    //getters__________________________
    public int getId() {return id;}
    public int getType() {return type;}
    public int getMaxPlayers() {return maxPlayers;}
    public int getInitStack() {return initStack;}
    public void setId(int id) {this.id = id;}
    public int getMinBid() {return minBid;}
    public Player getPlayer(String username){
        for(Player player : players){
            if(player.userName.equals(username)) return player;
        }
        throw new RuntimeException("player "+username+" is not found");
    }
    public Hand getTable() {
        if(table==null){
            table=new Hand();
        }
        return table;
    }
    //________________________________________

    public void setDealer(Player dealer) {
        this.dealer = dealer;
        for(Player player : players){
            if(player.userName.equals(dealer.userName)) {
                player.setDealer(true);
            }else {
                player.setDealer(false);
            }
        }
    }
    public void setCurrentPlayer(Player currentPlayer) {this.currentPlayer = currentPlayer;}
    public void setAllPlayersDidntPlay(){
        for(Player player : players){
            if(!player.hasFolded && !player.hasQuitted) player.setPlayedInThisTurn(false);
        }
    }
    public void setAllPlayersDidntFold(){
        for(Player player : players){
            if(!player.hasQuitted)player.setHasFolded(false);
        }
    }

    //________________________________________


    public String informationToString(int index){
        return "121 MESS "+index+" ID "+id+" "+type+" "+maxPlayers+" "+minBid+" "+initStack+" "+players.size();
    }

}
