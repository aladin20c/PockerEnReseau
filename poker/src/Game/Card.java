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
    public static Card createCard(String s){
        Suit suit=null;
        Rank rank=null;
        switch(s.charAt(0)){
            case 'D':
                suit = Suit.DIAMONDS;
                break;
            case 'C':
                suit= Suit.HEARTS;
                break;
            case 'S':
                suit = Suit.SPADES;
                break;
            case 'T':
                suit=Suit.CLUBS;
                break;

            default:
            //Lancer une exception
        }
        switch(s.substring(1)){
            case "1":
                rank = Rank.ACE;
                break;
            case "2":
                rank = Rank.DEUCE;
                break;
            case "3":
                rank = Rank.EIGHT;
                break;
            case "4":
                rank = Rank.FOUR;
                break;
            case "5":
                rank = Rank.FIVE;
                break;
            case "6":
                rank = Rank.SIX;
                break;
            case "7":
                rank = Rank.SEVEN;
                break;
            case "8":
                rank = Rank.EIGHT;
                break;
            case "9":
                rank = Rank.NINE;
                break;
            case "10":
                rank = Rank.TEN;
                break;
            case "11":
                rank = Rank.JACK;
                break;
            case "12":
                rank = Rank.QUEEN;
                break;
            case "13":
                rank = Rank.KING;
                break;
            
            default:
            //Lancer une exception
        }

        return new Card(rank,suit);
    }
    public boolean equals(Object o) {
        if(o==null || !(o instanceof Card)) return false;
        Card c = (Card)o;
        if (this==o || (this.rank==c.rank && this.suit==c.suit)) return true;
        return false;
      }

}
