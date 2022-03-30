package Game;

import Game.definitions.PokerHandType;
import Game.definitions.SortBy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Hand implements Comparable<Hand> {

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

    public boolean containsAll(Card[] cards){
        for (Card card : cards){
            if(! this.cards.contains(card)) return false;
        }
        return true;
    }

    public void addAll(Card[] cards){
        for (Card card : cards){
            this.cards.add(card);
        }
    }
    public void removeAll(Card[] cards){
        for (Card card : cards){
            this.cards.remove(card);
        }
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

    //TODO verify with all possibilities (check for flush hand (done))
    @Override
    public int compareTo(Hand hand) {
        if(handType.getPower()>hand.getHandType().getPower()){
            return 1;
        }
        if(handType.getPower()<hand.getHandType().getPower()){
            return -1;
        }
        for(int i=0;i< cards.size();i++){
            Card card=cards.get(i);
            Card otherHandsCard=hand.getCards().get(i);
            if(card.getRank().getRank()>otherHandsCard.getRank().getRank()){
                return 1;
            }else if(card.getRank().getRank()<otherHandsCard.getRank().getRank()){
                return -1;
            }
        }
        return 0;
    }
    public int nbCards(){
        return cards.size();
    }
    public Card getCard(int pos){
        return cards.get(pos);
    }
    /**
     * remove a card and return the same card
     * @param s
     * @return
     */
    public Card removeCard(String s){
        Card c = Card.createCard(s);
        for(Card card : cards){
            if(card.equals(c)){
                cards.remove(card);
                return c;
            }
        }
        return null;
    }
}
