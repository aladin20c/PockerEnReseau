package Game;

import java.util.ArrayList;

public abstract class PokerGame {
   protected ArrayList<Player> players;
   protected Player dealer;
   protected Player winner;
   protected Deck deck;
   protected int bidAmount=0;
   protected int totalBidOnTable=0;
   protected int bidTurn=0;
   protected int totalCheck=0;
   protected int foldedPlayers=0;
   protected boolean isBidRaised=false;
   protected boolean isOneTurnCompleted=false;
   protected boolean isGameFinished=false;

   public PokerGame(ArrayList<Player> players){
       deck = new Deck();
       isGameFinished=false;
       this.players=players;
       //creer une fonction qui initialise les joueur et pour chaque joueur on fait un setGame
   }

   public void makeBid(String betType, int raiseAmount, int callAmount){
       switch(betType){
            case "CALL":
                call(callAmount);
                break;
            case "RAISE":
                raise(raiseAmount,callAmount);
                break;
            default :
                //Lancer une exception
       }
   }
   public void makeBid(String betType) {
        switch(betType){
            case "CHECK":
                check();
               break;
            case "FOLD":
                fold();
               break;
            default :
             //Lancer une exception
        }
    }
    public void fold(){
        foldedPlayers++;
    }
    public void check(){
        int nbPlayer = players.size()-foldedPlayers;
        if(totalCheck == nbPlayer ){
            isOneTurnCompleted = true;
            bidTurn++;
            isBidRaised=false;
            totalCheck =0;
        }
        else{
            totalCheck++;
        }

    }
    public void call(int callAmount){
        totalBidOnTable += callAmount;
        check();

    }
    public void raise(int raiseAmount,int callAmount){
        bidAmount += raiseAmount;
        totalBidOnTable += raiseAmount+callAmount;
        if(!isBidRaised && totalCheck == 0)
        totalCheck++;
        else {
            totalCheck = 0;
        }
        isBidRaised = true;

    }

    public int getBidAmount(){
        return bidAmount;
    }
    public int getTotalCheck() {
        return totalCheck;
    }
    public int getFoldedPlayerNumber() {
        return foldedPlayers;
    }
    public void resetBidTurn() {
        bidTurn = 0;
        isBidRaised = false;
        totalCheck = 0;
        totalBidOnTable = 0;
        bidAmount = 0;
    }
    public boolean isOneTurnCompleted() {
        return isOneTurnCompleted;
    }
    public void setIsOneTurnCompleted(boolean b) {
        isOneTurnCompleted=b;
    }
    public void setBidAmount(int bidAmount) {
        this.bidAmount = bidAmount;
    }

    public int getTotalBidOnTable() {
        return totalBidOnTable;
    }
    public boolean isBidRaised() {
        return isBidRaised;
    }

    public abstract boolean isGameTurnFinished(); //Aredéfinir dans les classes 
    /*    if(this instanceof TexasHoldem){
            return bidTurn == 4;
        }
        else{
            return bidTurn == 4;
        }
       
    }*/
    public abstract void playGame();
    //public void initTableCard(); dans la classe TexasHoldem seulement
    public abstract boolean checkEndOfTurn(); //et affiche aussi le joueur gagnant
    public abstract void initPlayerDeck();
}
