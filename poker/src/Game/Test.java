package Game;

import Game.definitions.Rank;
import Game.definitions.Suit;
import Game.pokerhandranking.HandTypeRankingUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Test {
    public static void main (String [] args){
        Hand h=new Hand();

        Card c1=new Card(Rank.ACE, Suit.DIAMONDS);
        Card c2=new Card(Rank.THREE,Suit.HEARTS);
        Card c3=new Card(Rank.FOUR,Suit.CLUBS);
        Card c4=new Card(Rank.SIX,Suit.DIAMONDS);
        Card c5=new Card(Rank.EIGHT,Suit.SPADES);
        Card c6=new Card(Rank.QUEEN,Suit.CLUBS);
        Card c7=new Card(Rank.SEVEN,Suit.DIAMONDS);


        h.add(c1);
        h.add(c2);
        h.add(c3);
        h.add(c4);
        h.add(c5);
        h.add(c6);
        h.add(c7);

        //Collections.shuffle(h.cards);
        Hand d= HandTypeRankingUtil.getBestHand(h.cards);
        System.out.println(d.getHandType());
        System.out.println(d.cards);



    }
}
