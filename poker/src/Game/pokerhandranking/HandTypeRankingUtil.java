package Game.pokerhandranking;

import Game.Card;
import Game.Hand;
import Game.definitions.PokerHandType;
import Game.definitions.Rank;
import Game.definitions.Suit;

import java.util.List;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class HandTypeRankingUtil {
    public static Hand getBestHand(List<Card> cards){
        try{
            return isRoyalFlush(cards);
        }catch (Exception e){}
        try{
            return isStraightFlush(cards);
        }catch (Exception e){}
       /* try{
            return isFourOfaKind(cards);
        }catch (Exception e){}
        try{
            return isFullHouse(cards);
        }catch (Exception e){}
        try{
            return isFlush(cards);
        }catch (Exception e){}
        try{
            return isStraight(cards);
        }catch (Exception e){}
        try{
            return isThreeOfaKind(cards);
        }catch (Exception e){}
        try{
            return isTwoPair(cards);
        }catch (Exception e){}
        try{
            return isOnePair(cards);
        }catch (Exception e){}
*/
        return null;
    }

    /**
     * Analysis the cards and define if there is a royal flush combination
     * @param cards
     * @return
     */
    public static Hand isRoyalFlush(List<Card> cards) {
        Map<Suit,List<Card>> sortedCards=CardsManipUtil.suitClassification(cards);
        List<Card> royalFlushHand=null;
        for(Map.Entry<Suit,List<Card>> entry :sortedCards.entrySet()){
            if(entry.getValue().size()>= PokerHandType.ROYAL_FLUSH.getNbCardsRequired()){
                royalFlushHand=entry.getValue();
            }
        }
        if(royalFlushHand==null){
            throw new RuntimeException("Has not a royal flush hand");
        }
        /***********
         * METHODS TO TAKE LE 5 HIGHEST CARDS OF THE LIST
         */
        royalFlushHand=CardsManipUtil.getLongestConsecutiveSubList(royalFlushHand);
        int required = PokerHandType.ROYAL_FLUSH.getNbCardsRequired();
        if(royalFlushHand.size()<required){
            throw new RuntimeException("Has not a royal flush hand");
        }

        royalFlushHand=royalFlushHand.subList(royalFlushHand.size()-required,royalFlushHand.size());

        int counter=0;
        for(Rank r : EnumSet.range(Rank.TEN, Rank.ACE)) {
            if (r!= royalFlushHand.get(counter).getRank()) {
                throw new RuntimeException("Has not a royal flush hand");
            }
            counter++;
        }

        return new Hand(royalFlushHand,PokerHandType.ROYAL_FLUSH);
    }
    /**
     * Analysis the cards and define if there is a straight flush combination
     * @param cards
     * @return
     */
    public static Hand isStraightFlush(List<Card> cards){
        Map<Suit, List<Card>> sortedCards=CardsManipUtil.suitClassification(cards);//Classify the cards according to their suit
        List<Card> straightFlushHand=null;
        for(Map.Entry<Suit,List<Card>> entry:sortedCards.entrySet()){//Verify if there is a list of cards with the same suit has the size that allows her to form a straight flush
            if(entry.getValue().size()>=PokerHandType.STRAIGHT_FLUSH.getNbCardsRequired()){
                straightFlushHand=entry.getValue();
            }
        }
        if(straightFlushHand==null){
            throw new RuntimeException("Has not a straight flush hand");
        }
        straightFlushHand=CardsManipUtil.getLongestConsecutiveSubList(straightFlushHand);

        int numberOfRequiredCards=PokerHandType.STRAIGHT_FLUSH.getNbCardsRequired();
        int numberOfConsecutiveCards=straightFlushHand.size();

        if(numberOfConsecutiveCards>numberOfRequiredCards){
            straightFlushHand=straightFlushHand.subList(numberOfConsecutiveCards-numberOfRequiredCards,numberOfConsecutiveCards);
        }else if(numberOfConsecutiveCards<numberOfRequiredCards){//If the list of consecutive values does not satisfy the number of required cards
            throw new RuntimeException("Has not a straight flush hand");
        }

        return new Hand(straightFlushHand,PokerHandType.STRAIGHT_FLUSH);
    }
}
