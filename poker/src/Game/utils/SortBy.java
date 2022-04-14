package Game.utils;

import Game.Card;

import java.util.Comparator;

public enum SortBy {
    RANK(new Comparator<Card>(){

        @Override
        public int compare(Card first, Card second) {
            Integer firstVal=Integer.valueOf(first.getRank().getRank());
            Integer secondVal=Integer.valueOf(second.getRank().getRank());

            int comparison=firstVal.compareTo(secondVal);
            if(comparison==0){
                String firstSuit=first.getSuit().getShortName();
                String secondSuit=second.getSuit().getShortName();

                return firstSuit.compareTo(secondSuit);
            }

            return comparison;
        }
    }),
    SUIT(new Comparator<Card>(){

        @Override
        public int compare(Card first, Card second) {
            String firstSuit=first.getSuit().getShortName();
            String  secondSuit=second.getSuit().getShortName();

            int comparison=firstSuit.compareTo(secondSuit);
            if(comparison==0){
                Integer firstVal=Integer.valueOf(first.getRank().getRank());
                Integer secondVal=Integer.valueOf(second.getRank().getRank());

                return firstVal.compareTo(secondVal);
            }

            return comparison;
        }
    });

    private Comparator<Card> comparator;
    private SortBy(Comparator<Card> cardComparator) {
        this.comparator=cardComparator;
    }
     public Comparator<Card> getComparator(){
        return comparator;
     }

}
