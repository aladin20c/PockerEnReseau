package Game;

import java.util.ArrayList;

public class Hand {
    public ArrayList<Card> cards;

    //Constructor
    public Hand(){
        cards=new ArrayList<Card>();
    }

    public ArrayList<Card> getCards(){
        return cards;
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
    public int nbCards(){
        return cards.size();
    }
    public Card getCard(int pos){
        return cards.get(pos);
    }
}
