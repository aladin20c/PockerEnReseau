package Game;

public class Player {
    private String name;
    private int stack;
    private Hand userHand;
    private boolean isFold;
    private int bidPerRound=0;
    private PokerGame round;

    public Player(String name, int stack, PokerGame round){
        this.name=name;
        userHand = new Hand();
        isFold=false;
        this.stack=stack;
        this.round=round;
    }

    public void receiveCard(Card c){
        userHand.add(c);
    }
    public boolean canCall(){
        return ( round.getBidAmount()-bidPerRound)<=stack;
    }
    public boolean canCheck(){
        return bidPerRound== round.getBidAmount();
    }
    public void fold(){
        round.makeBid("FOLD");
        isFold=true;
    }
    public void call(){

    }
    public void raise(int raiseAmount){
        int callAmount = round.getBidAmount()-bidPerRound;
        bidPerRound += callAmount+raiseAmount;
        stack -= callAmount-raiseAmount;
        round.makeBid("RAISE", raiseAmount, callAmount);
    }
    public void check(){
        if(this.canCheck()){
            round.makeBid("CHECK");
        }
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

    
    
}