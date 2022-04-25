package Game;


public class Player {

    protected String name;
    protected int stack;
    protected Hand userHand;
    protected boolean isFold;
    protected boolean isQuit;
    protected boolean played;
    protected int bidPerRound=0;


    public Player(String name, int stack){
        this.name=name;
        this.stack=stack;
        userHand = new Hand();
        isFold=false;
        isQuit=false;
        played=false;
    }

    public void receiveCard(Card c){
        userHand.add(c);
    }
    public void addChipsToUser(int chips){
        stack+=chips;
    }


    public void fold(PokerGame round){
        isFold=true;
        played=true;
        round.incFolderPlayers();
        round.rotate();
    }

    public void check(PokerGame round){
        played=true;
        round.rotate();
    }

    public void call(PokerGame round){
        played=true;
        int callAmount = round.getBidAmount()-bidPerRound;
        bidPerRound += callAmount;
        stack -= callAmount;
        round.setPot(callAmount);
        round.rotate();
    }

    public void raise(PokerGame round,int raiseAmount){
        played=true;
        int callAmount = raiseAmount-bidPerRound;
        bidPerRound += callAmount;
        stack -= callAmount;
        round.setBidAmount(raiseAmount);
        round.setPot(callAmount);
        round.rotate();
    }

    public void quit(PokerGame round){
        this.played=true;
        this.isFold=true;
        this.isQuit=true;
        round.incFolderPlayers();
        if(round.getCurrentPlayer()==this){
            round.rotate();
        }
    }

    public void reset(){
        userHand.clear();
        isFold=false;
        played=false;
        bidPerRound=0;
    }

    public boolean canReplay(PokerGame game){
        return !hasQuitted() && this.stack>=game.minBid;
    }




    /**--------------------------------- Getters & Setters ---------------------------------*/

    public String getName() {
        return name;
    }
    public Hand getHand(){
        return userHand;
    }

    public int getBidPerRound() {
        return bidPerRound;
    }
    public int geStack() {
        return stack;
    }
    public void setBidPerRound(int bidAmount) {
        this.bidPerRound = bidAmount;
    }
    public void setUserHand(Hand userHand) {
        this.userHand = userHand;
    }


    public int getStack() {
        return stack;
    }

    public Card[] getCards(){
        Card[] cards = userHand.getCards().toArray(new Card[0]);
        return cards;
    }

    public boolean hasFolded() {
        return isFold;
    }
    public void setFold(boolean fold) {
        isFold = fold;
    }
    public boolean hasQuitted() {
        return isQuit;
    }
    public void setQuit(boolean isQuit) {
        this.isQuit= isQuit;
    }
    public boolean hasPlayed() {
        return played;
    }
    public void setPlayed(boolean played) {
        this.played= played;
    }

}

