package Game;

import Game.definitions.Rank;
import Game.definitions.Suit;
import Game.pokerhandranking.HandTypeRankingUtil;

import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main (String [] args){
        Hand h=new Hand();

        Card c1=new Card(Rank.DEUCE, Suit.DIAMONDS);
        Card c2=new Card(Rank.THREE,Suit.DIAMONDS);
        Card c3=new Card(Rank.FOUR,Suit.DIAMONDS);
        Card c4=new Card(Rank.FIVE,Suit.DIAMONDS);
        Card c5=new Card(Rank.ACE,Suit.DIAMONDS);
        Card c6=new Card(Rank.JACK,Suit.DIAMONDS);
        Card c7=new Card(Rank.QUEEN,Suit.DIAMONDS);
        Card c8=new Card(Rank.KING,Suit.DIAMONDS);


        h.add(c1);
        h.add(c2);
        h.add(c3);
        h.add(c4);
        h.add(c5);
        h.add(c6);
        h.add(c7);
        h.add(c8);

        Hand d= HandTypeRankingUtil.getBestHand(h.cards);
        System.out.println(d.getHandType());


    }
}
