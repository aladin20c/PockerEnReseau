package Game;

public class Game {
    public static void main (String [] args){
        Hand h3=new Hand();
        Hand h4=new Hand();
        Hand h[]=new Hand[2];
        h[0]=h3;
        h[1]=h4;

        Deck d=new Deck();
        d.populate();
        d.shuffle();
        d.deal(h,3);
        System.out.println("Distributing cards ");

        System.out.println("Hand 1 is :\n"+h[0].showHand()
                +" \nHand 2 is :\n"+h[1].showHand());

    }
}
