package Game.simulator;

import Game.Card;
import Game.Deck;
import Game.Hand;
import Game.utils.PokerHandType;
import Game.pokerhandranking.HandTypeRankingUtil;
import Game.simulator.Simulator.Data;

import java.util.*;


public class MonteCarlos {

    private static final int HANDS = 1000000;


    public static void testHandEvaluation(){

        //preparing simulation variables
        PokerHandType handType;
        Deck deck;
        FiveCards hand1;
        Hand hand2;

        //simulation loop
        for (int i = 0; i < 10000000; i++) {
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

        //preparing simulation variables
        HashSet<Card> cardSet=Simulator.getCardSet();
        ArrayList<Card> deck;

        int ahead = 0;
        int tied=0;
        int behind = 0;

        //preparink oppRanks
        Card[][] hands=new Card[nPlayers][5];
        int[] ranks=new int[nPlayers];


        //simulation loop
        for (int i = 0; i < HANDS; i++) {
            if (i>100000 && i%100000 == 0) System.out.println("simulated "+i+" random poker hands ...");
            if (i>0 && i<100000 && i%10000 == 0) System.out.println("simulated "+i+" random poker hands ...");
            deck=new ArrayList<>(cardSet);

            for (int k=0;k<nPlayers;k++){
                for (int j=0;j<5;j++){
                    hands[i][j]=deck.remove(0);
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
                behind--;
            }else if(istied){
                tied++;
            }else if(isahead){
                ahead++;
            }

        }
        System.out.println("ahead="+(double)ahead/10000);
        System.out.println("tied="+(double)tied/10000);
        System.out.println("behind="+(double)behind/10000);
        return new Data((double)ahead/10000,(double)tied/10000,(double)behind/10000);
    }







    public static void simulateFiveCardPokerDraw(Hand ourHand){

        //preparing simulation variables
        Map<PokerHandType, Integer> handTypeCountMap = new HashMap<>();
        PokerHandType handType;
        ArrayList<Card> cards;
        Deck deck;


        //simulation loop
        for (int i = 0; i < HANDS; i++) {
            if (i>100000 && i%100000 == 0) System.out.println("simulated "+i+" random poker hands ...");
            if (i>0 && i<100000 && i%10000 == 0) System.out.println("simulated "+i+" random poker hands ...");

            //create deck
            deck = new Deck();
            //burn card
            deck.burn();
            //deal a 2 card hand from the top of the deck
            cards = new ArrayList<>(deck.getNextCards(2));
            // deal a 5 card hand from the top of the deck
            deck.burn();
            cards.addAll(deck.getNextCards(3));
            deck.burn();
            cards.add(deck.getNextCard());
            deck.burn();
            cards.add(deck.getNextCard());

            //analyse handType
            handType = HandTypeRankingUtil.getBestHand(cards).getHandType();

            Integer count = handTypeCountMap.getOrDefault(handType,0);
            handTypeCountMap.put(handType, ++count);
        }

        System.out.println(handTypeCountMap);
    }





    public static void main(String[] args) {
        testHandEvaluation();
    }


}
