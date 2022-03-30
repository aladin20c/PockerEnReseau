package Game;
import java.util.ArrayList;

public abstract class PokerGame {


    protected static int COUNT=0;
    protected int id;
    protected int type;
    protected int maxPlayers;
    protected int minBid;
    protected int initStack;



    protected ArrayList<Player> players;
    protected ArrayList<Player> winners=new ArrayList<>();
    protected int currentPlayer;
    protected final int dealer=0;
    protected Deck deck;
    protected int bidAmount=0;//le bid maximum
    protected int pot=0;
    protected int foldedPlayers=0;
    protected int bidTurn=0;


    public PokerGame(int id, int type, int maxPlayers, int minBid, int initStack) {
        this.id = id;
        this.type = type;
        this.maxPlayers = maxPlayers;
        this.minBid = minBid;
        this.initStack = initStack;
        deck = new Deck();
        players=new ArrayList<Player>();
    }

    public PokerGame( int type, int maxPlayers, int minBid, int initStack) {
        this(++COUNT,type,maxPlayers,minBid,initStack);
    }


    public void addPlayer(String userName) {
        this.players.add(new Player(userName,initStack));
    }
    public void removePlayer(String userName) {
        for(Player player : players){
            if(player.getName().equals(userName)) {
                players.remove(player);
                return;
            }
        }
    }

    /**
     * To get the index of the next player
     * @return
     */
    public int nextPlayer(int i){
        int n=(i+1)%players.size();
        if(!players.get(n).hasFolded()){
            return n;
        }else{
            return nextPlayer(n);
        }
    }


    /**
     * increment tne number of foldedPlayers
     */
    public void incFolderPlayers(){
        foldedPlayers++;
    }

    /**
      * distribute nbCard to all players (no folded players)
      * @param nbCard
    */
     public void distributeCards(int nbCard){
         int nbPlayers = players.size()-foldedPlayers;
         Hand[] hands = new Hand[nbPlayers];
         int index = dealer;
         for(int i=0 ; i<nbPlayers ; i++){
             index=nextPlayer(index);
             hands[i]=players.get(index).getHand();
         }
         deck.dealPlayers(hands, nbCard);
    }


    public void rotate(){

        if(isTurnFinished()){
            bidTurn++;
            currentPlayer=nextPlayer(dealer);
            for(Player p : players){
                p.setPlayed(false);
            }
        }else{
            currentPlayer=nextPlayer(currentPlayer);
        }
    }


    public static  Player winner(ArrayList<Player> players){//todo return arraylist of players
         Player winner=players.get(0);
         for(int i=1;i<players.size();i++){
             if(players.get(i).getHand().compareTo(winner.getHand())==1){
                 winner=players.get(i);
             }
         }
         return winner;
    }
   

    public void resetGame(){
        players.add(players.remove(0));
        for(Player p:players){
            if(p.hasQuitted()){
                players.remove(p);
            } else{
                p.reset();
            }
        }
        deck=new Deck();
        winners.clear();
        currentPlayer=nextPlayer(dealer);
        bidTurn=0;
        pot=0;
        bidAmount=0;
        foldedPlayers=0;
    }


    public void burn(){
         this.deck.getNextCard();
    }



    /**--------------------------------- Getters & Setters ---------------------------------*/

    /**
     * add p to pot
     * @param p
     */
    public void incrementPot(int p){
        pot+=p;
    }

    public void setPot(int p){
        pot=0;
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
    public Player getDealer() { return players.get(0);}
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
    public ArrayList<Player> getWinners() {
        return winners;
    }
    /**
     * To set bidAmount
     * @param bidAmount
     */
    public void setBidAmount(int bidAmount) {
        this.bidAmount = bidAmount;
    }

    /**--------------------------------- methods to override ---------------------------------*/

    public boolean canChange(Player player,Card[] cards){
        return false;
    }
    public Card[] change(Player player,Card[] cards){
        return null;
    }
    public Card[] revealCards(int mbcards){return null;}
    public abstract boolean isRoundFinished();
    public abstract boolean isTurnFinished();
    public abstract boolean canResetGame();
    public abstract boolean canCall(Player player);
    public abstract boolean canCheck(Player player);
    public abstract boolean canFold(Player player);
    public abstract boolean canRaise(Player player,int raiseAmount );
}


