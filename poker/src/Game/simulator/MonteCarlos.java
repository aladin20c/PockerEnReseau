package Game.simulator;

import Game.Card;
import Game.Deck;
import Game.Hand;
import Game.utils.PokerHandType;
import Game.pokerhandranking.HandTypeRankingUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MonteCarlos {

    private static final int HANDS = 1000000;



    public static void simulateFiveCardPokerAnte(){

        //preparing simulation variables
        Map<PokerHandType, Integer> handTypeCountMap = new HashMap<>();
        PokerHandType handType;
        Deck deck;
        Hand hand;


        //simulation loop
        for (int i = 0; i < HANDS; i++) {
            if (i>100000 && i%100000 == 0) System.out.println("simulated "+i+" random poker hands ...");
            if (i>0 && i<100000 && i%10000 == 0) System.out.println("simulated "+i+" random poker hands ...");
            //create deck
            deck = new Deck();



            // deal a 5 card hand from the top of the deck
            hand = HandTypeRankingUtil.getBestHand(deck.getNextCards(5));



            //analyse handType
            handType= hand.getHandType();
            Integer count = handTypeCountMap.getOrDefault(handType,0);
            handTypeCountMap.put(handType, ++count);
        }

        // statically initialize all the expected (theoretical) probabilities for each type of hand
        Map<PokerHandType, Float> expectedProbabilities = new HashMap<>();
        long numberOfPossibleFiveCardHands = (52 * 51 * 50 * 49 * 48) / (5 * 4 * 3 * 2);
        expectedProbabilities.put(PokerHandType.STRAIGHT_FLUSH,    40F/numberOfPossibleFiveCardHands);
        expectedProbabilities.put(PokerHandType.FLUSH,           5108F/numberOfPossibleFiveCardHands);
        expectedProbabilities.put(PokerHandType.STRAIGHT,       10200F/numberOfPossibleFiveCardHands);
        expectedProbabilities.put(PokerHandType.FOUR_OF_A_KIND,   624F/numberOfPossibleFiveCardHands);
        expectedProbabilities.put(PokerHandType.FULL_HOUSE,      3744F/numberOfPossibleFiveCardHands);
        expectedProbabilities.put(PokerHandType.THREE_OF_A_KIND,54912F/numberOfPossibleFiveCardHands);
        expectedProbabilities.put(PokerHandType.TWO_PAIRS,      123552F/numberOfPossibleFiveCardHands);
        expectedProbabilities.put(PokerHandType.ONE_PAIR,     1098240F/numberOfPossibleFiveCardHands);
        printResultsToConsole(handTypeCountMap,expectedProbabilities);
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



    private static void printResultsToConsole(Map<PokerHandType, Integer> handTypeCountMap,Map<PokerHandType, Float> expectedProbabilities) {
        System.out.println("Probabilities:");
        float totalActualProbabilities = 0F;
        for (PokerHandType handType:  PokerHandType.values()) {
            String handTypeLabel = handType.getName();
            Integer handTypeCount = handTypeCountMap.get(handType);
            if (handTypeCount == null) {  // we never actually got any hands of this type
                handTypeCount = 0;
            }
            float actualProbability = ((float) handTypeCount) / (float) HANDS;
            totalActualProbabilities += actualProbability;
            if (handType != PokerHandType.NOTHING) {
                float expectedProbability = expectedProbabilities.getOrDefault(handType,0F);
                float deviation = (actualProbability - expectedProbability) / expectedProbability;
                System.out.println("\t" + handTypeLabel + "\t\t:   " + handTypeCount + " of  " + HANDS + ":  actual= " + 100 * actualProbability + "%    " +
                        "expected= " + 100 * expectedProbability + "%   deviation= " + 100 * deviation + "%");
            } else {
                System.out.println("\t" + handTypeLabel + "\t\t:   " + handTypeCount + " of  " + HANDS + ":  actual= " + 100 * actualProbability + "%    ");
            }
        }
        System.out.println("\nTotal of all actual proabilities= "+100 * totalActualProbabilities +"%");
    }


    public static void main(String[] args) {
        simulateFiveCardPokerAnte();
    }


}
