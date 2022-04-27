package Game.simulator;

import Game.Card;
import Game.Hand;
import Game.pokerhandranking.HandTypeRankingUtil;
import Game.utils.PokerHandType;
import Game.utils.Rank;
import Game.utils.Suit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public interface Simulator {


    static PokerHandType typeOfFiveCards(FiveCards hand) {
        PokerHandType handType=PokerHandType.HIGH_CARD;
        int paircount=hand.pairCount();
        boolean threeofakind=hand.isOfAKind(3);
        if (hand.isOfAKind(4)) {
            handType = PokerHandType.FOUR_OF_A_KIND;
        } else if ( threeofakind && paircount==1) {
            handType = PokerHandType.FULL_HOUSE;
        } else if (threeofakind) {
            handType = PokerHandType.THREE_OF_A_KIND;
        } else if (paircount==2) {
            handType = PokerHandType.TWO_PAIRS;
        } else if (paircount==1) {
            handType = PokerHandType.ONE_PAIR;
        } else {
            //  First we want to see if Aces can play low in straights and straight-flushes
            boolean straight= hand.isStraightFive();
            boolean flush= hand.isFlush();

            if (straight && flush) {
                if(hand.getCards().get(0).getRank()==Rank.TEN) handType = PokerHandType.ROYAL_FLUSH;
                else handType = PokerHandType.STRAIGHT_FLUSH;
            } else if (flush) {
                handType = PokerHandType.FLUSH;
            } else if (straight) {
                handType = PokerHandType.STRAIGHT;
            }
        }

        return handType;
    }

    static int rankFiveCards(FiveCards hand) {
        return typeOfFiveCards(hand).getPower()*19+hand.getHighestRank();
    }

    static int rankFiveCards(List<Card> cards) {
        FiveCards hand=new FiveCards(cards);
        return typeOfFiveCards(hand).getPower()*19+hand.getHighestRank();
    }

    static int rankHand(List<Card> cards) {
        Hand hand=HandTypeRankingUtil.getBestHand(cards);
        return hand.getHandType().getPower()*19+hand.getHighestRank();
    }

    /*gives a rank to a hand strenght*/
    static int rankHand(Hand hand){
        hand = HandTypeRankingUtil.getBestHand(hand.getCards());
        return hand.getHandType().getPower()*17+hand.getCards().get(0).getRank().getRank();
    }


    /*return hashset containing 52 poker cards*/
    static HashSet<Card> getCardSet(){
        HashSet<Card> set=new HashSet<>();
        for(Suit suit: Suit.values() ){
            for(Rank rank:Rank.values()){
                set.add(new Card(rank,suit));
            }
        }
        return set;
    }


    public static class Data{
        double ahead;
        double tied;
        double behind;

        public Data(double ahead, double tied, double behind) {
            this.ahead = ahead;
            this.tied = tied;
            this.behind = behind;
        }

        public Data() {
            this.ahead = -1;
            this.tied = -1;
            this.behind = -1;
        }
    }

}
