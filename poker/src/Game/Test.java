package Game;

import Game.utils.Rank;
import Game.utils.Suit;
import Game.pokerhandranking.HandTypeRankingUtil;

public class Test {
    public static void main (String [] args){
        Hand h=new Hand();
        Hand h2=new Hand();


        Card c1=new Card(Rank.DEUCE, Suit.DIAMONDS);
        Card c2=new Card(Rank.THREE,Suit.DIAMONDS);
        Card c3=new Card(Rank.FOUR,Suit.DIAMONDS);
        Card c4=new Card(Rank.QUEEN,Suit.DIAMONDS);
        Card c5=new Card(Rank.ACE,Suit.DIAMONDS);
        Card c6=new Card(Rank.ACE,Suit.HEARTS);

        h.add(c1);
        h.add(c2);
        h.add(c3);
        h.add(c4);
        h.add(c5);
        h.add(c6);

        Card c21=new Card(Rank.DEUCE, Suit.DIAMONDS);
        Card c22=new Card(Rank.THREE,Suit.DIAMONDS);
        Card c23=new Card(Rank.FOUR,Suit.DIAMONDS);
        Card c24=new Card(Rank.KING,Suit.DIAMONDS);
        Card c25=new Card(Rank.ACE,Suit.DIAMONDS);
        Card c26=new Card(Rank.ACE,Suit.HEARTS);

        h2.add(c21);
        h2.add(c22);
        h2.add(c23);
        h2.add(c24);
        h2.add(c25);
        h2.add(c26);

        Hand analyse1= HandTypeRankingUtil.getBestHand(h.cards);
        System.out.println(analyse1.getHandType());
        System.out.println(analyse1.getCards());

        Hand analyse2= HandTypeRankingUtil.getBestHand(h2.cards);
        System.out.println(analyse2.getHandType());
        System.out.println(analyse2.getCards());

        System.out.println(analyse1.compareTo(analyse2));



    }
}
