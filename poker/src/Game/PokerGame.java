package Game;
import java.util.ArrayList;

public abstract class PokerGame {

    protected static final int dealer=0;
    protected static int COUNT=0;
    protected int id;
    protected int type;
    protected int maxPlayers;
    protected int minBid;
    protected int initStack;


    protected ArrayList<Player> players;
    protected ArrayList<Player> winners;
    protected int currentPlayer;
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
        this.deck = new Deck();
        this.players=new ArrayList<Player>();
        this.winners=new ArrayList<>();
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
    public void removePlayer(Player player) {
        players.remove(player);
    }


    /*public int nextPlayer(int i){//gives me error
        int n=(i+1)%players.size();
        if(!players.get(n).hasFolded()){
            return n;
        }else{
            return nextPlayer(n);
        }
    }*/
    public int nextPlayer(int n){
        for(int i=1;i<players.size();i++){
            int index=(n+i)%players.size();
            if(! players.get(index).hasFolded()) return index;
        }
        return n;
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
         if(isRoundFinished()) {
             bidTurn=4;
             return;
         }else if(isTurnFinished()){
            bidTurn++;
            currentPlayer=nextPlayer(dealer);
            for(Player p : players){
                p.setPlayed(false);
            }
        }else{
            currentPlayer=nextPlayer(currentPlayer);
        }
    }


    public  void setWinners(){
        for(Player player: players){
            if(player.hasQuitted() || player.hasFolded()) {
                continue;
            }if(winners.isEmpty()){
                winners.add(player);
            }else{
                int cmp=player.getHand().compareTo(winners.get(0).getHand());
                switch (cmp){
                    case 0 :
                        winners.add(player);
                        break;
                    case 1 :
                        winners.clear();
                        winners.add(player);
                        break;
                }
            }
        }
    }
   

    public void resetGame(){
        int moneyPart=this.pot/ winners.size();
        for (Player p : winners) p.addChipsToUser(moneyPart);
        players.add(players.remove(0));
        for(Player p:players){
            if(p.hasQuitted()){
                players.remove(p);
            }else{
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
    public boolean isTurnFinished(){
        for (Player p : players) {
            if (!p.hasFolded()) {
                if (!p.played || p.bidPerRound != bidAmount) {
                    return false;
                }
            }
        }
        return true;
    }


    /**--------------------------------- Getters & Setters ---------------------------------*/

    /**
     * add p to pot
     * @param p
     */
    public void setPot(int p){
        pot+=p;
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

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public int getType() {
        return type;
    }

    public Player getPlayer(String username){
        for(Player player : players){
            if(player.name.equals(username)) return player;
        }
        throw new RuntimeException("player "+username+" is not found");
    }

    public Hand getTable(){return null;}

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getMinBid() {
        return minBid;
    }

    public int getInitStack() {
        return initStack;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public int getBidTurn() {return bidTurn;}

    public boolean isCurrentPlayer( String name){
        return players.get(currentPlayer).getName().equals(name);
    }

    public int getFoldedPlayers() {return foldedPlayers;}

    /**--------------------------------- methods to override ---------------------------------*/

    public Card[] revealCards(int mbcards){return null;}
    public boolean canChange(Player player,Card[] cards){
        return false;
    }
    public Card[] change(Player player,Card[] cards){
        return null;
    }
    public abstract boolean isRoundFinished();
    public abstract boolean canResetGame();
    public abstract boolean canStartGame();
    public abstract boolean canCall(Player player);
    public abstract boolean canCheck(Player player);
    public abstract boolean canFold(Player player);
    public abstract boolean canRaise(Player player,int raiseAmount );
}


