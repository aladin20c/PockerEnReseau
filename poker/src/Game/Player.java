package Game;

public class Player {
    private String name;
    private int stack;
    private Hand userHand;
    private boolean isFold;
    private int bidPerRound=0;
    private PokerGame round;
    private boolean played;

    public Player(String name, int stack){
        this.name=name;
        userHand = new Hand();
        isFold=false;
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
    /**
     * check if this player can do a CALL
     * @return
     */
    public boolean canCall(){
        if(this==round.getCurrentPlayer()){
            return (round.getBidAmount()-bidPerRound)<=stack;
        }
        return false;
    }
    /**
     * check if this player can do a CHECK
     * @return
     */
    public boolean canCheck(){
        if(this==round.getCurrentPlayer()){
            return (bidPerRound == round.getBidAmount());
        }
        return false;
    }
    /**
     * check if this player can do a RAISE
     * @param raiseAmount
     * @return
     */
    public boolean canRaise(int raiseAmount){
        if(this==round.getCurrentPlayer()){
            int callAmount = round.getBidAmount()-bidPerRound;
            return (round.getBidAmount()- callAmount-raiseAmount)<=stack;
        }
        return false;
    }
    /**
     * check if this player can do a FOLD
     * @return
     */
    public boolean canFold(){
        return (this==round.getCurrentPlayer());
    }
    public void fold(){
        isFold=true;
        if(this==round.getDealer()){
            round.incFolderPlayers();
        }
        round.rotate();
    }
    public void call(){
        int callAmount = round.getBidAmount()-bidPerRound;
        bidPerRound += callAmount;
        stack -= callAmount;
        round.setPot(callAmount);
        round.incTotalCheck();
        round.rotate();
    }
    public void raise(int raiseAmount){
        int callAmount = round.getBidAmount()-bidPerRound;
        bidPerRound = bidPerRound + callAmount+raiseAmount;
        stack = stack- callAmount-raiseAmount;
        round.setBidAmount(round.getBidAmount()+raiseAmount);
        round.setPot(raiseAmount+callAmount);
        round.setTotalCheck(1);
        round.rotate();
    }
    public void check(){
        round.incTotalCheck();
        round.rotate();
    }
    public void addChipsToUser(int chips){
        stack+=chips;
    }


    public Hand getHand(){
        return userHand;
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
    
    
}