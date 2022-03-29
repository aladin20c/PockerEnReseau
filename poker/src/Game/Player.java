package Game;

public class Player {
    private String name;
    private int stack;
    private Hand userHand;
    private boolean isFold;
    private int bidPerRound=0;
    private boolean isQuit;
    private boolean played;
    //achaque fois je teste si le joueur n a pas folder et n as pas quiter

    public Player(String name, int stack){
        this.name=name;
        userHand = new Hand();
        isFold=false;
        isQuit=false;
        this.stack=stack;
        played=false;
    }
    /**
     * add card c to the hand of Player
     * @param c
     */
    public void receiveCard(Card c){
        userHand.add(c);
    }
    public void fold(PokerGame round){
        isFold=true;
        round.incFolderPlayers();
        round.rotate();
    }
    public void call(PokerGame round){
        int callAmount = round.getBidAmount()-bidPerRound;
        bidPerRound += callAmount;
        stack -= callAmount;
        round.setPot(callAmount);
        round.rotate();
    }
    public void raise(PokerGame round,int raiseAmount){
        int callAmount = raiseAmount-bidPerRound;
        bidPerRound += callAmount;
        stack -= callAmount;
        round.setBidAmount(raiseAmount);
        round.setPot(callAmount);
        round.rotate();
    }
    public void check(PokerGame round){
        round.rotate();
    }
    public void addChipsToUser(int chips){
        stack+=chips;
    }


    public Hand getHand(){
        return userHand;
    }
    public void resetHand(){
        userHand.clear(); 
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

    public void setFold(boolean fold) {
        isFold = fold;
    }

    public void setUserHand(Hand userHand) {
        this.userHand = userHand;
    }

    public boolean isFold() {
        return isFold;
    }
    public Hand getUserHand() {
        return userHand;
    }

    public int getStack() {
        return stack;
    }

    public String getName() {
        return name;
    }
    public void setRound(PokerGame round){
        this.round=round;
    }
    public void setIsQuit(boolean isQuit) {
        this.isQuit= isQuit;
    }
    public boolean isQuit() {
        return isQuit;
    }
    public void setPlayed(boolean played) {
        this.played= played;
    }
    public boolean played() {
        return played;
    }
}