package Game.simulator;

import Game.Card;
import Game.Deck;
import Game.Hand;
import Game.utils.PokerHandType;
import Game.pokerhandranking.HandTypeRankingUtil;


import java.util.*;


public class MonteCarlos {

    public static void testHandEvaluation(){

        //preparing simulation variables
        PokerHandType handType;
        Deck deck;
        FiveCards hand1;
        Hand hand2;

        //simulation loop
        for (int i = 0; i < 1000000; i++) {
            if (i>100000 && i%100000 == 0) System.out.println("simulated "+i+" random poker hands ...");
            if (i>0 && i<100000 && i%10000 == 0) System.out.println("simulated "+i+" random poker hands ...");
            //create deck
            deck = new Deck();



            // deal a 5 card hand from the top of the deck
            ArrayList<Card> cadrds=deck.getNextCards(5);

            hand1 = new FiveCards(cadrds);
            handType= Simulator.typeOfFiveCards(hand1);

            hand2=HandTypeRankingUtil.getBestHand(cadrds);

            if(handType!=hand2.getHandType()){
                System.out.println("first hand "+handType.getName());
                System.out.println("second hand "+hand2.getHandType().getName());
                StringBuilder cards= new StringBuilder("cards : ");
                for (Card c : cadrds){
                    cards.append(c.toString()).append(" ");
                }
                System.out.println(cards);
                throw new RuntimeException("different hands");
            }
        }
        System.out.println("correct hand evaluation for five card hands");
    }


    public static Data simulateAnte(int turn, int nPlayers){
        int tries=100000;
        //preparing simulation variables
        long begin=System.nanoTime();
        HashSet<Card> cardSet=Simulator.getCardSet();
        ArrayList<Card> deck;

        int ahead = 0;
        int tied=0;
        int behind = 0;

        Card[][] hands=new Card[nPlayers][5];
        int[] ranks=new int[nPlayers];


        for (int i = 0; i < tries; i++) {
            deck=new ArrayList<>(cardSet);
            Collections.shuffle(deck);



            for (int j=0;j<5;j++){
                for (int k=0;k<nPlayers;k++) {
                    hands[(k+1)%nPlayers][j] = deck.remove(0);
                }
            }


            for (int k=0;k<nPlayers;k++){
                ranks[k]=Simulator.rankFiveCards(Arrays.asList(hands[k]));
            }

            //ranking hands
            boolean isahead=false;
            boolean istied=false;
            boolean isbehind=false;

            int ourrank=ranks[turn-1];
            for (int k=0;k<ranks.length;k++){
                if(k==turn-1) continue;
                if(ourrank<ranks[k]){
                    isbehind=true;
                    break;
                }else if(ourrank==ranks[k]){
                    istied=true;
                }else{
                    isahead=true;
                }
            }

            if(isbehind){
                behind++;
            }else if(istied){
                tied++;
            }else if(isahead){
                ahead++;
            }

        }
        long end=System.nanoTime();
        double time=((double)(end-begin))/1000000000;
        return (new Data(tries,time,(double)ahead*100/tries,(double)tied*100/tries,(double)behind*100/tries));
    }

}
