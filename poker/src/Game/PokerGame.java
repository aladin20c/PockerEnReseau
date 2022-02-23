package Game;
import java.util.ArrayList;

public class PokerGame{
    private ArrayList<Player> players;
    private Player currentPlayer;
    private Hand table;
    private Deck deck;
    private int pot;
    //private pokerHandEvaluator evaluator;
    //private dealer dealer;

    public PokerGame(){
        players=new ArrayList<Player>();
        currentPlayer=null;
        table=new Hand();
        deck=new Deck();
        pot=0;
        //evaluator=new PokerHandEvaluator();
        //dealer=new Dealer();
    }
    public Player getCurrentPlayer(){
        return currentPlayer;
    }
    public void setCurrentPlayer(Player player){
        currentPlayer=player;
    }
    public void addPlayer(Player player){
        players.add(player);
        setCurrentPlayer(players.get(0));
    }
    public Player nextPlayer(){
        int position=players.indexOf(currentPlayer);
        if(position<(players.size()-1)){
            setCurrentPlayer( players.get(position+1));
        }else{
            setCurrentPlayer( players.get(0));
        }
        return currentPlayer;
    }
    public ArrayList<Player> getPlayers(){
        return players;
    }
    public int getPot(){
        return pot;
    }
    public void setPot(int p){
        pot=p;
    }
    public void addToPot(int bet){
        pot+=bet;
    }
    public Hand getTable(){
        return table;
    }
    public void clearTable(){
        table.clear();
    }
    public void clearHands(){
        for(Player p:players){
            p.getHand().clear();
        }
    }
    public ArrayList<Hand> getPlayerHands(){
        ArrayList<Hand> playerHands=new ArrayList<Hand>();
        for(Player p:players){
            playerHands.add(p.getHand());
        }
        return playerHands;
    }
    public Hand combineHandAndTable(Player player){
        Hand handAndTable=new Hand();
        for(Card c:table.getCards()){
            table.give(c,handAndTable);
        }
        for(Card c:player.getHand().getCards()){
            player.getHand().give(c,handAndTable);
        }
        return handAndTable;
    }

}
