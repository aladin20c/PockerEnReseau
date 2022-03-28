package Game;
import java.util.ArrayList;
import java.util.Scanner;
public abstract class PokerGame {
    protected ArrayList<Player> players;
    protected int currentPlayer;
    protected int dealer;
    protected Player winner=null;
    protected Deck deck;
    protected int bidAmount=0;
    protected int pot=0;
    protected int totalCheck=0;
    protected int foldedPlayers=0;
    protected boolean isOneTurnCompleted=false;
    protected boolean isGameFinished=false;
    protected int bidTurn=0;
    protected Scanner sc;
 
    public PokerGame(ArrayList<Player> players){
        deck = new Deck();
        this.players=players;
        dealer=0;
        initPlayer();
        sc = new Scanner(System.in);
    }
 
    /**
     * To get the deck
     * @return
    */
    public Deck getDeck(){
        return deck;
    }
    /**
     * to get the currentPlayer
     * @return
     */
    public Player getCurrentPlayer() { return players.get(currentPlayer);}
    /**
     * to get the currentPlayer
     * @return
     */
    public Player getDealer() { return players.get(dealer);}
    /**
     * To get the bidAmount
     * @return
     */
    public int getBidAmount(){
        return bidAmount;
    }
    /**
    * To get pot
    * @return
    */
      public int getPot() {
        return pot;
    }
    /**
     * To get the winner
     * @return
     */
    public Player getWinner() {
        return winner;
    }
    /**
      * To set bidAmount
      * @param bidAmount
    */
    public void setBidAmount(int bidAmount) {
        this.bidAmount = bidAmount;
    }
    /**
     * set totalCheck
     * @param n
     */
    public void setTotalCheck(int n){
        totalCheck=n;
    }
    /**
     * change the dealer
     */
    public void setNextDealer(){
        dealer=nextPlayer(dealer);
    }
    /**
     * To get the index of the next player
     * @return
     */
    public int nextPlayer(int i){
         int n=(i+1)%players.size();
         if(!players.get(n).isFold()){
             return n;
         }
         else{
             return nextPlayer(n);
         }
    }
    public Player nextDealer(){
        return players.get(nextPlayer(dealer));
   }
    /**
      * increment tne number of foldedPlayers
    */
    public void incFolderPlayers(){
        foldedPlayers++;
    }
    /**
     * increment totalCheck
    */
    public void incTotalCheck(){
        totalCheck++;
    }
    /**
     * add p to pot
     * @param p
    */
    public void setPot(int p){
        pot+=p;
    }    
    /**
     * Initialize the attribut PokerGame of each player
     */
    public void initPlayer(){
         for(Player p : players){
             p.setRound(this);
         }
    }
    /**
     * reset bidPerRound of each player
     */
    public void resetBidPerRoundOfPlayers(){
        for(Player p:players){
            p.setBidPerRound(0);
        }
    }
    /**
      * distribute nbCard to all players (no folded players)
      * @param nbCard
    */
     public void distributeCard(int nbCard){
         int nbPlayers = players.size()-foldedPlayers;
         Hand[] hands = new Hand[nbPlayers];
         int index = dealer;
         for(int i=0 ; i<nbPlayers ; i++){
             index=nextPlayer(index);
             hands[i]=players.get(index).getHand();
         }
         deck.dealPlayers(hands, nbCard);
    }
    public boolean isTurnFinished(){
        if(totalCheck==(players.size()-foldedPlayers)){
            return true;
        }
        if(players.size()-foldedPlayers==1){
            for(Player p : players){
                if(!p.isFold()){
                    winner=p;
                    return true;
                }
            }
        }
        return false;
    }
    public void rotate(){
        if(isTurnFinished()){
            bidTurn++;
            bidAmount=0;
            resetBidPerRoundOfPlayers();
            currentPlayer=nextPlayer(dealer);
        }
        else{
            currentPlayer=nextPlayer(currentPlayer);
        }
    }

    public void defineWinner(){
         for(Player p:players){
            if(p.getHand().compareTo(this.winner.getHand())==1){
                winner=p;
            }
         }
    }

    public static Player winner(ArrayList<Player> players){
         Player winner=players.get(0);
         for(int i=1;i<players.size();i++){
             if(players.get(i).getHand().compareTo(winner.getHand())==1){
                 winner=players.get(i);
             }
         }
         return winner;
    }
    public abstract boolean isRoundFinished();

    public boolean canChange(Player player,ArrayList<Card> cards){
        return (cards.size()>=1
                && cards.size()<=5
                && player.getHand().getCards().containsAll(cards)
                && isOneTurnCompleted
                && players.get(currentPlayer).getName()==player.getName());

    }
    public ArrayList<Card> change(Player player,ArrayList<Card> cards){
        //if(canChange(player,cards))
        deck.getCards().addAll(cards);
        ArrayList<Card> newCards=new ArrayList<>();
        for(int i=0 ;i<cards.size();i++){
            newCards.add(deck.getNextCard());
        }
        player.getHand().getCards().addAll(newCards);
        return newCards;
    }
}
