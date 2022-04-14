package Game;

import Game.utils.Rank;
import Game.utils.Suit;

import java.util.ArrayList;
import java.util.Collections;

public class Deck extends Hand {

    public Deck() {
        populate();
        shuffle();
    }

    /**
     * Initialise the deck with all the possible values
     */
    public void populate(){
        for(Suit suit: Suit.values() ){
            for(Rank rank:Rank.values()){
                Card card=new Card(rank,suit);
                this.add(card);
            }
        }
    }

    /**
     * To reorder the list
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * To distribute cards to the players
     */
    public void dealPlayers(Hand [] hands,int perHand){
        for(int i=0;i<perHand;i++){
            for(int j=0;j<hands.length;j++){
                this.give(cards.get(0),hands[j]);
            }
        }
    }
    /**
     * To put the cards on the table
     */
    public void deal(Hand table,int perHand){
        for(int i=0;i<perHand;i++){
            this.give(cards.get(0),table);
        }
    }
    /**
     * To burn the first card
     */
    public void burn(){
        cards.remove(0);
    }

    /**
     * Number of cards left
     * @return
     */
    public int getNumberCardsLeft(){
        return cards.size();
    }

    /**
     * Get the next card
     */
    public Card getNextCard(){
        if(cards.size()!=0){
            return cards.remove(0);
        }
        return null;
    }




    public ArrayList<Card> getNextCards(int n){
        ArrayList<Card> nextcards=new ArrayList<>();
        for (int i=0;i<n;i++){
            nextcards.add(this.cards.remove(0));
        }
        return nextcards;
    }


}
