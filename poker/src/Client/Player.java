package Client;

import Game.Card;
import Game.Hand;
import Game.Room;

public class Player{


    public String userName;
    public Hand hand;

    public int stack;
    public int bids;

    public boolean hasFolded;
    public boolean hasQuitted;
    public boolean playedInThisTurn;
    public boolean dealer;



    public Player(String userName, int stack) {
        this.userName = userName;
        this.stack = stack;
        this.bids=0;
        this.hasFolded=false;
        this.hasQuitted=false;
        this.dealer=false;
    }



    public Hand getHand() {
        if(hand==null){
            this.hand=new Hand();
        }
        return hand;
    }



    public void setHasFolded(boolean hasFolded) {this.hasFolded = hasFolded;}
    public void setInactive(){
        this.hasFolded=true;
        this.hasQuitted=true;
        this.playedInThisTurn=true;
    }
    public void setDealer(boolean dealer) {this.dealer = dealer;}
    public void setPlayedInThisTurn(boolean playedInThisTurn) {this.playedInThisTurn = playedInThisTurn;}



    public void fold(){
        setHasFolded(true);
    }
    public void check(){

    }
    public void call(Room room) {
        int call=room.highestBid-bids;
        stack-=call;
        room.pot+=call;
        bids = room.highestBid;
    }
    public void raise(Room room,int raise){
        stack -= raise;
        bids += raise;
        room.pot+=raise;
        room.highestBid=bids;
    }
    public boolean canFold(){return !hasFolded;}
    public boolean canCheck(Room room){
        return !hasFolded && bids==room.highestBid ;
    }
    public boolean canCall(Room room) {
        return !hasFolded && bids < room.highestBid && stack >= (room.highestBid - bids) ;
    }
    public boolean canRaise(Room room,int raise){
        return !hasFolded && bids <= room.highestBid && stack >= raise && (raise+stack)>room.highestBid;
    }
    public boolean canChange(int numberOfCards) {
        return !hasFolded && numberOfCards<=5 ;
    }

    public  void discardCards(Card[] cards){
        for (Card c : cards){
            hand.getCards().remove(c);
        }
    }
    public  void addCards(Card[] cards){
        for (Card c : cards){
            hand.getCards().add(c);
        }
    }

}
