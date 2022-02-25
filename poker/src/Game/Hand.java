package Game;

import Game.definitions.PokerHandType;
import Game.definitions.SortBy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Hand {
    protected List<Card> cards;
    private PokerHandType handType;

    //Constructor
    public Hand(){
        cards=new ArrayList<>();
        handType=PokerHandType.NOTHING;
    }

    public Hand(List<Card> cards, PokerHandType handType){
        this.cards=cards;
        this.handType=handType;
    }
    public List<Card> getCards(){
        return cards;
    }
    public PokerHandType getHandType(){
        return handType;
    }
    /**
     * To clear the list
     */
    public void clear(){
        cards.clear();
    }

    /**
     * Add a new card to the list
     * @param c
     */
    public void add(Card c){
        cards.add(c);
    }

    /**
     * To show all the faceUp cards
     * @return
     */
    public String showHand(){
        String str="";
        for(Card c:cards){
            str+=c.toString()+"\n";
        }
        return str;
    }

    /**
     * Tp give a card to another hand (player for example)
     * @param c
     * @param otherHand
     * @return
     */
    public boolean give(Card c,Hand otherHand){
        if(!cards.contains(c)){
            return false;
        }
        cards.remove(c);
        otherHand.add(c);
        return true;
    }

    /**
     * Make all the cards visible
     */
    public void flipCards(){
        for(Card c:cards){
            c.flipCard();
        }
    }

    /**
     * Sort cards according to theirs suit
     */
    public void sortBySuit(){
        Collections.sort(cards, SortBy.SUIT.getComparator());
    }

    /**
     * Sort cards according to theirs rank
     */
    public void sortByRank(){
        Collections.sort(cards,SortBy.RANK.getComparator());
    }

    public boolean has5Cards(){
        return cards.size()==5;
    }
    //********CHECKING FOR HANS'S VALUES***********//

    public boolean isFlush(){
        sortBySuit();
        int size= cards.size();
        return cards.get(0).getSuit().equals(cards.get(size-1).getSuit());//If the first and last cards have the same suit
    }
/*
    public boolean isStraight(){
        sortByRank();
        if(cards.get(0).getRank().getRank()== Rank.ACE.getRank()){
                boolean weekStr=(cards.get(1).getRank()==Rank.DEUCE.getRank())
                    &&(cards.get(2).getRank()==Rank.THREE.getRank())
                    &&(cards.get(3).getRank()==Rank.FOUR.getRank())
                    &&(cards.get(4).getRank()==Rank.FIVE.getRank());
            boolean strongStr=(cards.get(1).getRank()==Rank.TEN.getRank())
                    &&(cards.get(2).getRank()==Rank.JACK.getRank())
                    &&(cards.get(3).getRank()==Rank.QUEEN.getRank())
                    &&(cards.get(4).getRank()==Rank.KING.getRank());
            return (weekStr || strongStr) ;
        }else{
            int size= cards.size();
            int rank=cards.get(0).getRank();
            return cards.get(size-1).getRank()==(rank+5)-1;
        }
    }

    public boolean isRoyalFlush(){
        int size= cards.size();
        sortByRank();
        return (isFlush() && isStraight()
                && (cards.get(0).getRank()==Rank.ACE.getRank()
                && cards.get(size-1).getRank()==Rank.KING.getRank() ));
    }
*/
}
