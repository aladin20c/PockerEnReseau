package Game;
import java.util.ArrayList;
public abstract class PokerGame {
    protected ArrayList<Player> players;
    protected int currentPlayer;
    protected final int dealer;
    protected Player winner=null;
    protected Deck deck;
    protected int minBid;
    protected int bidAmount=0;//le bid maximum
    protected int pot=0;
    protected int foldedPlayers=0;
    protected int bidTurn=0;
 
    public PokerGame(int minBid){
        this.minBid=minBid;
        deck = new Deck();
        players=new ArrayList<Player>();
        dealer=0;
    }
    public void addPlayer(Player player){
        players.add(player);
    }
    public Player firstPlayer(){
        return players.get(nextPlayer(dealer));
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
     * add p to pot
     * @param p
    */
    public void setPot(int p){
        pot+=p;
    }
    /**
      * distribute nbCard to all players (no folded players)
      * @param nbCard
    */
     public void distributeCard(int nbCard){
         if(nbCard==2) deck.burn();
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
        if(players.size()-foldedPlayers==1){
            for(Player p : players){
                if(!p.isFold()){
                    winner=p;
                    p.addChipsToUser(pot);
                    return true;
                }
            }
        }
        //je parcours tous les joueurs et je teste si les joueurs ont joué et ont misé la meme somme
        for(Player p : players){
            if(!p.isFold()){
                if(!p.played || p.getBidPerRound!=bidAmount){
                    return false;
                }
            }
        }
        return true;
    }
    public void rotate(){
        if(isTurnFinished()){
            bidTurn++;
            currentPlayer=nextPlayer(dealer);
            for(Player p : players){
                p.setPlayed(false);
            }
        }
        else{
            currentPlayer().setPlayed(true);
            currentPlayer=nextPlayer(currentPlayer);
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
   

    public boolean canChange(Player player,ArrayList<Card> cards){
        return false;

    }
    public ArrayList<Card> change(Player player,ArrayList<Card> cards){
        return null;
    }

    
    public void resetGame(){
        players.add(players.get(dealer));
        players.remove(dealer);
        for(Player p:players){
            if(p.isQuit()){
                players.remove(p);
            }
            else{
                p.setIsFold(false);
                p.setPlayed(false);
                p.setBidPerRound(0);
                p.resetHand();
                //on doit initialiser aussi le stack de chaque joueur donc on doit garder la trace
            }
        }
        bidTurn=0;
        pot=0;
        bidAmount=0;
        foldedPlayers=0;
        currentPlayer=nextPlayer(dealer);
        deck=new Deck();
        winner=null;
    }
    public abstract boolean canResetGame();
    public abstract boolean isRoundFinished();
    public abstract boolean canCall(Player player);
    public abstract boolean canCheck(Player player);
    public abstract boolean canFold(Player player);
    public abstract boolean canRaise(Player player,int raiseAmount );
}
