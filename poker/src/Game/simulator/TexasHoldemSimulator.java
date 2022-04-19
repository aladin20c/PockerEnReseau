package Game.simulator;

import Game.Card;
import Game.Hand;
import Game.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class TexasHoldemSimulator extends Simulator{

    /*naive simulation for texas holdem
    we assume that every player already got 2 cards*/
    public void simulate(Hand playerhand, Hand tablehand, ArrayList<Player> gameplayers , int numeberOfPlayers){

        //preparing necessary cards for simulation
        HashSet<Card> cardSet=getCardSet();
        tablehand.getCards().forEach(cardSet::remove);
        playerhand.getCards().forEach(cardSet::remove);


        //preparing simulation variables
        int ahead = 0;
        int tied=0;
        int behind = 0;


        ArrayList<Card> ourhand = new ArrayList<>(playerhand.getCards());
        ArrayList<Card>[] opphands = new ArrayList[numeberOfPlayers-1];
        for (int i = 0; i < numeberOfPlayers; i++) {
            opphands[i] = new ArrayList<>();
            opphands[i].add(null);
            opphands[i].add(null);
        }
        ArrayList<Card> boardhand=new ArrayList<>();
        ArrayList<Card> deck;

        //simulation
        for(int i=0;i<100000;i++){

            deck = new ArrayList<>(cardSet);
            boardhand.clear();
            boardhand.addAll(tablehand.getCards());
            deck.remove(0);
            distributeCards(deck,opphands,2);

            int n=boardhand.size();
            if(n==0){
                deck.remove(0);
                boardhand.add(deck.remove(0));
                boardhand.add(deck.remove(0));
                boardhand.add(deck.remove(0));
                deck.remove(0);
                boardhand.add(deck.remove(0));
                deck.remove(0);
                boardhand.add(deck.remove(0));
            }else if(n==3 ){
                deck.remove(0);
                boardhand.add(deck.remove(0));
                deck.remove(0);
                boardhand.add(deck.remove(0));
            }else if (n==4){
                deck.remove(0);
                boardhand.add(deck.remove(0));
            }


            //ranking hands
            boolean isahead=false;
            boolean istied=false;
            boolean isbehind=false;
            int ourrank=Rank(ourhand,boardhand);
            int opprank=0;

            for (ArrayList<Card> hand : opphands){
                opprank=Rank(hand,boardhand);
                if(ourrank>opprank) {
                    isahead=true;
                }else if (ourrank==opprank){
                    istied=true;
                }else {
                    isbehind=true;
                    break;
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

        System.out.println("ahead="+ahead);
        System.out.println("tied="+tied);
        System.out.println("behind="+behind);
    }









    /*calculating handStrength*/
    public int HandStrength(ArrayList<Card> ourcards, ArrayList<Card> boardcards) {
        int ourrank = Rank(ourcards, boardcards);
        int ahead = 0;
        int tied = 0;
        int behind = 0;

        //available cards
        HashSet<Card> cardSet=getCardSet();
        ourcards.forEach(cardSet::remove);
        boardcards.forEach(cardSet::remove);
        Card[] cards=new Card[cardSet.size()];
        cardSet.toArray(cards);


        //Consider all two card combinations of the remaining cards.
        ArrayList<Card> oppcards = new ArrayList<>();
        oppcards.add(null);
        oppcards.add(null);
        int opprank;


        for (int i = 0; i < cards.length-1; i++) {
            for (int j =i+1; j < cards.length; j++) {

                oppcards.set(0,cards[i]);
                oppcards.set(1,cards[j]);
                opprank = Rank(oppcards, boardcards);
                if (ourrank > opprank) ahead += 1;
                else if (ourrank == opprank) tied += 1;
                else behind += 1;
            }
        }
        //return hand strength
        return (ahead + tied / 2) / (ahead + tied + behind);
    }





    /*calculating handPotential*/
    public int HandPotential(ArrayList<Card> ourcards, ArrayList<Card> boardcards) {

        //Hand potential array, and behind.
        int index;
        int ahead = 0;
        int tied = 1;
        int behind = 2;
        int[][] HP = new int[3][3];//initialize to 0
        int[] HPTotal = new int[3];//initialize to 0
        int ourrank = Rank(ourcards, boardcards);


        //available cards
        HashSet<Card> cardSet=getCardSet();
        ourcards.forEach(cardSet::remove);
        boardcards.forEach(cardSet::remove);
        Card[] cards=new Card[cardSet.size()];
        cardSet.toArray(cards);

        //Consider all two card combinations of the remaining cards for the opponent.
        ArrayList<Card> oppcards = new ArrayList<>();
        oppcards.add(null);
        oppcards.add(null);
        int opprank;

        for (int i = 0; i < cards.length-1; i++) {
            for (int j = i + 1; j < cards.length; j++) {

                oppcards.set(0,cards[i]);
                oppcards.set(1,cards[i]);

                opprank = Rank(oppcards, boardcards);
                if (ourrank > opprank) index = ahead;
                else if (ourrank == opprank) index = tied;
                else index = behind;
                HPTotal[index] += 1;

                ArrayList<Card> plusCards=new ArrayList<>();
                plusCards.add(null);
                plusCards.add(null);
                // All possible board cards to come.
                for (int k=0;k<cards.length-1;k++) {//for each case(turn)
                    if(k==i|| k==j) continue;
                    for (int l=k+1;l<cards.length;l++) {//for each case(river)
                        if(l==i|| l==j) continue;

                        plusCards.set(0,cards[k]);
                        plusCards.set(1,cards[j]);

                        int ourbest = Rank(ourcards, boardcards,plusCards);
                        int oppbest = Rank(oppcards, boardcards,plusCards);

                        if (ourbest > oppbest) HP[index][ahead] += 1;
                        else if (ourbest == oppbest) HP[index][tied] += 1;
                        else HP[index][behind] += 1;
                    }
                }
            }
        }
        //PPot: were behind but moved ahead
        int PPot = (HP[behind][ahead] + HP[behind][tied] / 2 + HP[tied][ahead] / 2) / (HPTotal[behind] + HPTotal[tied] / 2);
        //NPot: were ahead but fell behind.
        int NPot = (HP[ahead][behind] + HP[tied][behind] / 2 + HP[ahead][tied] / 2) / (HPTotal[ahead] + HPTotal[tied] / 2);
        //HS:Hand Strength
        int HS=(HPTotal[ahead] + HPTotal[tied] / 2) / (HPTotal[ahead] + HPTotal[tied] + HPTotal[behind]);
        //EHS:Effective Hand Strength
        int EHS = HS * (1 - NPot) + (1 - HS) * PPot;
        //EHS:Effective Hand Strength Reduit
        int EHSR = HS + (1 - HS) * PPot;
        System.out.println("ppot:"+PPot+" npot:"+NPot+" HS:"+HS+" EHS:"+EHS+" EHSR:"+EHSR);
        return 1;
    }

}
