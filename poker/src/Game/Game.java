package Game;

public class Game {
    public static void main (String [] args){
        Hand h=new Hand();


        Card c1=new Card(Rank.KING,Suit.DIAMONDS);
        Card c2=new Card(Rank.QUEEN,Suit.DIAMONDS);
        Card c3=new Card(Rank.JACK,Suit.DIAMONDS);
        Card c4=new Card(Rank.TEN,Suit.DIAMONDS);
        Card c5=new Card(Rank.ACE,Suit.DIAMONDS);

        h.add(c1);
        h.add(c2);
        h.add(c3);
        h.add(c4);
        h.add(c5);

        System.out.println(h.isRoyalFlush());



    }
}
