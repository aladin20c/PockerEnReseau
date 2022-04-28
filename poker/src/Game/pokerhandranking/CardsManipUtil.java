package Game.pokerhandranking;

import Game.Card;
import Game.utils.Rank;
import Game.utils.SortBy;
import Game.utils.Suit;

import java.util.*;

public class CardsManipUtil {
    /**
     * To organise the cards according to theirs rank
     *
     * @param cards
     * @return
     */
    public static Map<Rank, List<Card>> rankClassification(List<Card> cards) {
        Map<Rank, List<Card>> rankDistribution = new HashMap<>();
        for (Card c : cards) {
            if (rankDistribution.containsKey(c.getRank())) {
                rankDistribution.get(c.getRank()).add(c);
            } else {
                List<Card> l = new ArrayList<>();
                l.add(c);
                rankDistribution.put(c.getRank(), l);
            }
        }
        return rankDistribution;
    }

    /**
     * To organise the cards according to theirs suit
     *
     * @param cards
     * @return
     */
    public static Map<Suit, List<Card>> suitClassification(List<Card> cards) {
        Map<Suit, List<Card>> suitDistribution = new HashMap<>();
        for (Card c : cards) {
            if (suitDistribution.containsKey(c.getSuit())) {
                suitDistribution.get(c.getSuit()).add(c);
            } else {
                List<Card> l = new ArrayList<>();
                l.add(c);
                suitDistribution.put(c.getSuit(), l);
            }
        }
        return suitDistribution;
    }

    /**
     * To combine two hands (table + player's hand for example
     *
     * @param hand
     * @param table
     * @return
     */
    public static List<Card> merge(List<Card> hand, List<Card> table) {
        List<Card> merge = new ArrayList<>();
        if(hand!=null ) {
            merge.addAll(hand);
        }
        if( table!=null){
            merge.addAll(table);
        }
        return merge;
    }

    public static List<Card> remove(List<Card> cards, List<Card> toRemove) {
        List<Card> afterRemove = new ArrayList<>();
        if (toRemove == null || toRemove.isEmpty()) {
            afterRemove.addAll(cards);
            return afterRemove;
        }
        for (Card c : cards) {
            if (!toRemove.contains(c)) {
                afterRemove.add(c);
            }
        }
        return afterRemove;
    }

    public static List<Card> getHighestSubListExcept(int size, List<Card> cards, List<Card> toRemove) {
        List<Card> highest = remove(cards, toRemove);
        Collections.sort(highest, SortBy.RANK.getComparator());
/*
        //If we have ace 2 3 ... we need to put the ACE at the beginning
        // Its rank order is 14 (the highest one) so we need to replace it
        // when it precedes deuce
        Card lastCard=highest.get(highest.size()-1);
        if(lastCard.getRank()==Rank.ACE && highest.get(0).getRank()==Rank.DEUCE){
            highest.add(0,lastCard);
            highest.remove(highest.size()-1);
        }
 */
       Collections.reverse(highest);//TODO pay attention to the order
        if (highest.size() <= size) {
            return highest;
        }
        return highest.subList(0, size);
    }


    /**
     * Helps to get the longest and highest consecutive cards of the list cards
     * @param cards
     * @return
     */
    public static List<Card> getLongestConsecutiveSubList(List<Card> cards) {
        List<Card> sorted = removeDuplicatesAndSortByRank(cards);//We need first to remove duplicates

        List<Card> longest = new ArrayList<>();

        //If we find an ACE we should add another copy at the beginning
        //in order to verify the txo possible combination 1 2 3... or
        if(sorted.get(sorted.size()-1).getRank()==Rank.ACE){
            sorted.add(0,sorted.get(sorted.size()-1));
        }
        int startPosition = 0;
        Card previousCard = null;
        for (int i = 0; i < sorted.size(); i++) {
            if (previousCard==null
                    ||((previousCard.getRank().getRank()+1 != sorted.get(i).getRank().getRank())
                        && !(previousCard.getRank() == Rank.ACE
                        && sorted.get(i).getRank() == Rank.DEUCE)))
            {

                if (i - startPosition >= longest.size()) {
                    longest =sorted.subList(startPosition, i);
                }
                startPosition = i;
            }
            previousCard = sorted.get(i);
        }

        /*If the last element is also in the subList
            For example l={2d,3d,8d,9d,10d,Jd,Qd,Kd}
            startPosition=2 it will be pointing to the 8d=>i=2
            SO when i=7 we won't get into the condition as result
            it won't consider the last element Kd which is in the
            consecutive subList(we need to add it to the sub list),
            That s why we verify the last case
            (Because if Kd(the last element) was not in the subList
            the startPosition will point exactly to its index,it will
            satisfy the if condition)
        */
        if ((startPosition < sorted.size() - 1)
                && (sorted.size() - startPosition >= longest.size())) {
            longest = sorted.subList(startPosition, sorted.size());
        }

        return longest;
    }

    public static List<Card> removeDuplicatesAndSortByRank(List<Card> cards){
        List<Card> sorted=sortBy(SortBy.RANK,cards);
        List<Card> sortedByRankWithoutDuplicates =new ArrayList<>();
        for(int i=0;i<sorted.size()-1;i++){
            if(sorted.get(i).getRank()!=sorted.get(i+1).getRank()){
                sortedByRankWithoutDuplicates.add(sorted.get(i));
            }
        }
        int sizeSorted=sorted.size();
        int sizeSortedWithoutDuplicates=sortedByRankWithoutDuplicates.size();
        if(sorted.get(sizeSorted-1).getRank()!=sortedByRankWithoutDuplicates.get(sizeSortedWithoutDuplicates-1).getRank()){
            sortedByRankWithoutDuplicates.add(sorted.get(sizeSorted-1));
        }
        return sortedByRankWithoutDuplicates;
    }

    public static List<Card> sortBy(SortBy sort, List<Card> cards){
        List<Card> copy=new ArrayList<>(cards);
        Collections.sort(copy,sort.getComparator());
        return copy;
    }
}
