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
        //if(hand.getCards().size()!=5) throw new RuntimeException("not 5 "+hand.getCards().size());
        return typeOfFiveCards(hand).getPower()*19+hand.getHighestRank();
    }

    static int rankFiveCards(List<Card> cards) {
        FiveCards hand=new FiveCards(cards);
        return typeOfFiveCards(hand).getPower()*19+hand.getHighestRank();
    }

    static int rankTexasHoldem(List<Card> hand,List<Card> board){
        ArrayList<Card> E=new ArrayList<>();
        for (Card c : hand){
            if(c!=null) E.add(c);
        }
        for (Card c : board){
            if(c!=null) E.add(c);
        }
        Hand h=HandTypeRankingUtil.getBestHand(E);
        return h.getHandType().getPower()*19+h.getHighestRank();
    }

    static int rankTexasHoldem(List<Card> hand,List<Card> board,Card card_1,Card card_2){
        ArrayList<Card> E=new ArrayList<>();
        for (Card c : hand){
            if(c!=null) E.add(c);
        }
        for (Card c : board){
            if(c!=null) E.add(c);
        }
        if(card_1!=null) E.add(card_1);
        if(card_2!=null) E.add(card_2);
        Hand h=HandTypeRankingUtil.getBestHand(E);
        return h.getHandType().getPower()*19+h.getHighestRank();
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



}
