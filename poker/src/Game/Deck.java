package Game;

import java.util.Random;

public class Deck extends Hand {
    Random rand =new Random();

    /**
     * Initialise the deck with all the possible values
     */
    public void populate(){
        for(Suit suit: Suit.values() ){
            for(Rank rank:Rank.values()){
                Card card=new Card(rank,suit);
                card.flipCard();
                this.add(card);
            }
        }
    }

    /**
     * To reorder the list
     */
    public void shuffle() {
        for (int i=cards.size()-1;i>0;i--){
           int pick=rand.nextInt(i);
           Card randCard=cards.get(pick);
           Card lastCard=cards.get(i);
           cards.set(pick,lastCard);
           cards.set(i,randCard);
        }
    }

    /**
     * To distribute cards to the hands
     */
    public void deal(Hand [] hands,int perHand){
        for(int i=0;i<perHand;i++){
            for(int j=0;j<hands.length;j++){
                this.give(cards.get(0),hands[j]);
            }
        }
    }

}
