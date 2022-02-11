package Game;

public class Card {

    //Private fields
    private Suit suit;
    private Rank rank;
    private boolean isFaceUp;

    //Constructor
    public Card(Rank rank, Suit suit){
        this.rank=rank;
        this.suit=suit;
        isFaceUp=true;
    }

    //Public methods

    /**
     * To get the suit
     * @return
     */
    public String getSuit(){
        return suit.printSuit();
    }

    /**
     * To get the rank
     * @return
     */
    public int getRank(){
        return rank.getRank();
    }

    /**
     * To get the information of the card (suit and rank)
     * if it's faceUp (visible)
     * @return
     */
    public String toString(){
        String str="";
        if(isFaceUp) {
            str+=rank.printRank()+" of "+suit.printSuit();
        }else{
            str="Face down (nothing to see here)";
        }
        return str;
    }
    /**
     * To return a card and change its position
    */
    public void flipCard(){
        isFaceUp=!isFaceUp;
    }
}
