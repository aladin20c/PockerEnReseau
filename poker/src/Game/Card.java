package Game;

import Game.definitions.Rank;
import Game.definitions.Suit;

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

    public Card (String s){
        switch(s.charAt(0)){
            case 'D':
                this.suit = Suit.DIAMONDS;
                break;
            case 'C':
                this.suit= Suit.HEARTS;
                break;
            case 'S':
                this.suit = Suit.SPADES;
                break;
            case 'T':
                this.suit=Suit.CLUBS;
                break;

            default: throw new RuntimeException("unrecognized suit");
        }
        switch(s.substring(1)){
            case "1":
                this.rank = Rank.ACE;
                break;
            case "2":
                this.rank = Rank.DEUCE;
                break;
            case "3":
                this.rank = Rank.EIGHT;
                break;
            case "4":
                this.rank = Rank.FOUR;
                break;
            case "5":
                this.rank = Rank.FIVE;
                break;
            case "6":
                this.rank = Rank.SIX;
                break;
            case "7":
                this.rank = Rank.SEVEN;
                break;
            case "8":
                this.rank = Rank.EIGHT;
                break;
            case "9":
                this.rank = Rank.NINE;
                break;
            case "10":
                this.rank = Rank.TEN;
                break;
            case "11":
                this.rank = Rank.JACK;
                break;
            case "12":
                this.rank = Rank.QUEEN;
                break;
            case "13":
                this.rank = Rank.KING;
                break;
            default: throw new RuntimeException("unrecognized value");
        }
    }



    //Public methods

    /**
     * To get the suit
     * @return
     */

    public Suit getSuit(){
        return suit;
    }

    /**
     * To get the rank
     * @return
     */
    public Rank getRank(){
        return rank;
    }

    /**
     * To get the information of the card (suit and rank)
     * if it's faceUp (visible)
     * @return
     */
    public String toString(){
        int rank=(this.rank.getRank()-1)%13+1;
        return rank+suit.getShortName();
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

    public boolean encodedTo(String s) {
        if(s.length()!=2) return false;
        return this.getSuit().getShortName().equals(String.valueOf(s.charAt(0))) &&
                String.valueOf(this.getRank().getRank()).equals(String.valueOf(s.charAt(1)));
    }
}
