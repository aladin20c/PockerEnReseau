package Game.simulator;

import Game.Card;
import Game.Hand;
import Game.pokerhandranking.HandTypeRankingUtil;
import Game.utils.Rank;
import Game.utils.Suit;

import java.util.ArrayList;
import java.util.HashSet;

public interface Simulator {



    public void simulate();

    /*gives a rank to a hand strenght*/
    public static int Rank(Hand hand){
        //return 1;
        hand = HandTypeRankingUtil.getBestHand(hand.getCards());
        return hand.getHandType().getPower()*17+hand.getCards().get(0).getRank().getRank();
    }

    /*return hashset containing 52 poker cards*/
    public static HashSet<Card> getCardSet(){
        HashSet<Card> set=new HashSet<>();
        for(Suit suit: Suit.values() ){
            for(Rank rank:Rank.values()){
                set.add(new Card(rank,suit));
            }
        }
        return set;
    }

}
