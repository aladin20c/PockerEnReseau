package Game.pokerhandranking;

import Game.Card;
import Game.Hand;
import Game.definitions.PokerHandType;
import Game.definitions.Rank;
import Game.definitions.Suit;

import java.util.*;
import java.util.List;

public class HandTypeRankingUtil {
    public static Hand getBestHand(List<Card> cards){
        try{
            return isRoyalFlush(cards);
        }catch (Exception e){}
        try{
            return isStraightFlush(cards);
        }catch (Exception e){}
        try{
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
        /*try{
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
    private static Hand isRoyalFlush(List<Card> cards) {
        Map<Suit,List<Card>> sortedCards=CardsManipUtil.suitClassification(cards);
        List<Card> royalflushHand=null;
        for(Map.Entry<Suit,List<Card>> entry :sortedCards.entrySet()){
            if(entry.getValue().size()>= PokerHandType.ROYAL_FLUSH.getNbCardsRequired()){
                royalflushHand=entry.getValue();
                break;
            }
        }
        if(royalflushHand==null){
            throw new RuntimeException("Has not a royal flush hand");
        }
        /***********
         * METHODS TO TAKE LE 5 HIGHEST CARDS OF THE LIST
         */
        royalflushHand=CardsManipUtil.getLongestConsecutiveSubList(royalflushHand);
        int required = PokerHandType.ROYAL_FLUSH.getNbCardsRequired();
        if(royalflushHand.size()<required){
            throw new RuntimeException("Has not a royal flush hand");
        }

        royalflushHand=royalflushHand.subList(royalflushHand.size()-required,royalflushHand.size());

        int counter=0;
        for(Rank r : EnumSet.range(Rank.TEN, Rank.ACE)) {
            if (r!= royalflushHand.get(counter).getRank()) {
                throw new RuntimeException("Has not a royal flush hand");
            }
            counter++;
        }

        return new Hand(royalflushHand,PokerHandType.ROYAL_FLUSH);
    }
    /**
     * Analysis the cards and define if there is a straight flush combination
     * @param cards
     * @return
     */
    private static Hand isStraightFlush(List<Card> cards){
        Map<Suit, List<Card>> sortedCards=CardsManipUtil.suitClassification(cards);//Classify the cards according to their suit
        List<Card> straightFlushHand=null;
        for(Map.Entry<Suit,List<Card>> entry:sortedCards.entrySet()){//Verify if there is a list of cards with the same suit has the size that allows her to form a straight flush
            if(entry.getValue().size()>=PokerHandType.STRAIGHT_FLUSH.getNbCardsRequired()){
                straightFlushHand=entry.getValue();
                break;
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
    private static Hand isFourOfaKind(List<Card> cards) {
        Map<Rank, List<Card>> sortedCards=CardsManipUtil.rankClassification(cards);//Classify the cards according to their rank
        List<Card>fourOfaKindHand=null;
        for(Map.Entry<Rank,List<Card>> entry:sortedCards.entrySet()){
            if(entry.getValue().size()>=PokerHandType.FOUR_OF_A_KIND.getNbCardsRequired()){
                fourOfaKindHand=entry.getValue();
                break;
            }
        }
        if(fourOfaKindHand==null){
            throw new RuntimeException("Has not a four of a kind hand");
        }
        List<Card> lastCard=CardsManipUtil.getHighestSubListExcept(
                5-PokerHandType.FOUR_OF_A_KIND.getNbCardsRequired(),
                cards,
                fourOfaKindHand);

        fourOfaKindHand.add(lastCard.get(lastCard.size()-1));
        return new Hand(fourOfaKindHand,PokerHandType.FOUR_OF_A_KIND);
    }
    private static Hand isFullHouse(List<Card> cards) {
        Map<Rank, List<Card>> sortedCards=CardsManipUtil.rankClassification(cards);//Classify the cards according to their rank
        List<Card> twoOfaKindHand=null;
        List<Card> threeOfaKindHand=null;
        for(Map.Entry<Rank,List<Card>> entry:sortedCards.entrySet()){
            if(entry.getValue().size()>=PokerHandType.THREE_OF_A_KIND.getNbCardsRequired()
                &&(threeOfaKindHand==null
                    ||(threeOfaKindHand!=null
                        && threeOfaKindHand.get(0).getRank().getRank()<entry.getKey().getRank()))){
                threeOfaKindHand=entry.getValue();
            }
        }
        for(Map.Entry<Rank,List<Card>> entry:sortedCards.entrySet()){
            if(entry.getValue().size()>=PokerHandType.ONE_PAIR.getNbCardsRequired()
                    &&((threeOfaKindHand!=null && threeOfaKindHand.get(0).getRank()!=entry.getKey())
                    && (twoOfaKindHand==null
                        || twoOfaKindHand!=null && twoOfaKindHand.get(0).getRank().getRank()<entry.getKey().getRank()))){
                twoOfaKindHand=entry.getValue();
            }
        }

        if(twoOfaKindHand==null || threeOfaKindHand==null){
            throw new RuntimeException("Has not a full house hand");
        }

        List<Card> fullHouseHand=new ArrayList<>();
        threeOfaKindHand=threeOfaKindHand.subList(0,PokerHandType.THREE_OF_A_KIND.getNbCardsRequired());
        twoOfaKindHand=twoOfaKindHand.subList(0,PokerHandType.ONE_PAIR.getNbCardsRequired());
        fullHouseHand.addAll(threeOfaKindHand);
        fullHouseHand.addAll(twoOfaKindHand);

        return new Hand(fullHouseHand, PokerHandType.FULL_HOUSE);

    }

    private static Hand isFlush(List<Card> cards) {
        Map<Suit, List<Card>> sortedCards=CardsManipUtil.suitClassification(cards);//Classify the cards according to their suit
        List<Card> flushHand=null;
        for(Map.Entry<Suit,List<Card>> entry:sortedCards.entrySet()){//Verify if there is a list of cards with the same suit has the size that allows her to form a straight flush
            if(entry.getValue().size()>=PokerHandType.FLUSH.getNbCardsRequired()){
               flushHand=entry.getValue();
               break;
            }
        }
        if(flushHand==null){
            throw new RuntimeException("Has not a flush hand");
        }
        flushHand= CardsManipUtil.getHighestSubListExcept(
                PokerHandType.FLUSH.getNbCardsRequired(),
                flushHand,null);
        return new Hand(flushHand,PokerHandType.FLUSH);
    }
    private static Hand isStraight(List<Card> cards) {
        List<Card> straightFlushHand=null;

        straightFlushHand=CardsManipUtil.getLongestConsecutiveSubList(cards);

        if(straightFlushHand.size()<PokerHandType.STRAIGHT.getNbCardsRequired()){//If the list of consecutive values does not satisfy the number of required cards
            throw new RuntimeException("Has not a straight hand");
        }

        if(straightFlushHand.size()>PokerHandType.STRAIGHT.getNbCardsRequired()){
            straightFlushHand=straightFlushHand.subList(straightFlushHand.size()-PokerHandType.STRAIGHT.getNbCardsRequired(),
                    straightFlushHand.size());

        }
        return new Hand(straightFlushHand,PokerHandType.STRAIGHT);

    }
    private static Hand isThreeOfaKind(List<Card> cards) {
        Map<Rank, List<Card>> sortedCards=CardsManipUtil.rankClassification(cards);//Classify the cards according to their rank
        List<Card> threeOfaKindHand=null;
        for(Map.Entry<Rank,List<Card>> entry:sortedCards.entrySet()){
            if(entry.getValue().size()>=PokerHandType.THREE_OF_A_KIND.getNbCardsRequired()
                    &&(threeOfaKindHand==null
                        ||(threeOfaKindHand!=null
                            && threeOfaKindHand.get(0).getRank().getRank()<entry.getKey().getRank()))){
                threeOfaKindHand=entry.getValue();
            }
        }
        if(threeOfaKindHand==null){
            throw new RuntimeException("Has not a three of a kind hand");
        }

        List<Card> twoOthers=CardsManipUtil.getHighestSubListExcept(
                5-PokerHandType.THREE_OF_A_KIND.getNbCardsRequired(),
                cards,
                threeOfaKindHand);
        threeOfaKindHand.addAll(twoOthers);

        return new Hand(threeOfaKindHand, PokerHandType.THREE_OF_A_KIND);

    }




}
