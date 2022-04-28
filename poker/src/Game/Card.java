package Game;

import Game.utils.Rank;
import Game.utils.Suit;

public class Card implements Comparable<Card>{

    //Private fields
    private final Suit suit;
    private final Rank rank;

    //Constructor
    public Card(Rank rank, Suit suit){
        this.rank=rank;
        this.suit=suit;
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
                this.rank = Rank.THREE;
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
            case "14":
                this.rank = Rank.ACE;
                break;
            default: throw new RuntimeException("unrecognized value");
        }
    }



    public boolean isAce(){return rank==Rank.ACE;}

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
        return suit.getShortName()+rank;
    }

    public boolean equals(Object o) {
        if(!(o instanceof Card)) return false;
        Card c = (Card)o;
        return this == o || (this.rank == c.rank && this.suit == c.suit);
    }


    @Override
    public int compareTo(Card o) {
        if(rank==o.rank){
            return 0;
        }else if(rank.getRank()>o.rank.getRank()){
            return 1;
        }else{
            return -1;
        }
    }
}
