package Game.simulator;

import Game.Card;
import Game.utils.Rank;
import Game.utils.Suit;

import java.util.ArrayList;
import java.util.HashSet;

public class Simulator {

    /*return hashset containing 52 poker cards*/
    public HashSet<Card> getCardSet(){
        HashSet<Card> set=new HashSet<>();
        for(Suit suit: Suit.values() ){
            for(Rank rank:Rank.values()){
                set.add(new Card(rank,suit));
            }
        }
        return set;
    }

    /*gives a rank to a hand strenght*/
    public int Rank(ArrayList<Card> handcards, ArrayList<Card> tablecards) {
        return 1;
    }


    public void distributeCards(ArrayList<Card> cards, ArrayList<Card>[] hands, int perHand){
        for(int i=0;i<perHand;i++){
            for (ArrayList<Card> hand : hands) {
                hand.set(i, cards.remove(0));
            }
        }
    }
}
