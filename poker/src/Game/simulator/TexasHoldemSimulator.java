package Game.simulator;

import Game.Card;
import Game.Hand;
import Game.Player;

import java.util.*;

public class TexasHoldemSimulator implements Simulator{



    public void simulate(){

    }


    /*naive simulation for texas holdem we assume that every player already got 2 cards*/
    public void simulate(Player ourPlayer, Hand tablehand, ArrayList<Player> players){

        //preparing necessary cards for simulation
        HashSet<Card> cardSet=Simulator.getCardSet();
        tablehand.getCards().forEach(cardSet::remove);
        ourPlayer.getHand().getCards().forEach(cardSet::remove);
        Card[] boardhand=new Card[5];
        int tableSize=0;
        for (Card c :tablehand.getCards()){
            boardhand[tableSize]=c;
            tableSize+=1;
        }
        ArrayList<Card> deck;


        //preparing simulation variables
        int ahead = 0;
        int tied=0;
        int behind = 0;


        //preparing all player hands
        Hand ourHand=new Hand(ourPlayer.getHand().getCards());
        LinkedHashMap<Player,Hand> oppHands=new LinkedHashMap<>();
        for (int i=0;i<=players.size();i++){
            Player p=players.get(i%players.size());
            if (p!=ourPlayer){
                oppHands.put(p,new Hand());
            }
        }



        //simulation
        for(int i=0;i<1000000;i++){

            deck = new ArrayList<>(cardSet);
            Collections.shuffle(deck);
            for (Map.Entry<Player, Hand> entry : oppHands.entrySet()) {
                entry.getValue().clear();
            }


            deck.remove(0);
            for (int cnt=0;cnt<5;cnt++){
                for (Map.Entry<Player, Hand> entry : oppHands.entrySet()) {
                    if(!entry.getKey().getHand().isEmpty()){
                        entry.getValue().add(deck.remove(0));
                        entry.getValue().add(deck.remove(0));
                    }
                }
            }

            if(tableSize==0){
                deck.remove(0);
                boardhand[tableSize]=deck.remove(0);
                boardhand[tableSize+1]=deck.remove(0);
                boardhand[tableSize+2]=deck.remove(0);
                deck.remove(0);
                boardhand[tableSize+3]=deck.remove(0);
                deck.remove(0);
                boardhand[tableSize+4]=deck.remove(0);
            }else if(tableSize==3 ){
                deck.remove(0);
                boardhand[tableSize]=deck.remove(0);
                deck.remove(0);
                boardhand[tableSize+1]=deck.remove(0);
            }else if (tableSize==4){
                deck.remove(0);
                boardhand[tableSize]=deck.remove(0);
            }


            //ranking hands
            boolean isahead=false;
            boolean istied=false;
            boolean isbehind=false;
            int ourrank=0;//fixme Rank(ourHand,boardhand);
            int opprank=0;

            for (Map.Entry<Player, Hand> entry : oppHands.entrySet()) {
                if(!entry.getKey().hasFolded()){
                    opprank=0;//fixme Simulator.Rank(entry.getValue(),boardhand);
                    if(ourrank>opprank) {
                        isahead=true;
                    }else if (ourrank==opprank){
                        istied=true;
                    }else {
                        isbehind=true;
                        break;
                    }
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
        int ourrank =0; //fixme Rank(ourcards, boardcards);
        int ahead = 0;
        int tied = 0;
        int behind = 0;

        //available cards
        HashSet<Card> cardSet=Simulator.getCardSet();
        ourcards.forEach(cardSet::remove);
        boardcards.forEach(cardSet::remove);

        Card[] deck=new Card[cardSet.size()];
        cardSet.toArray(deck);


        //Consider all two card combinations of the remaining cards.
        Card[] oppcards = new Card[2];
        int opprank;


        for (int i = 0; i < deck.length-1; i++) {
            oppcards[0]=deck[i];
            for (int j =i+1; j < deck.length; j++) {
                oppcards[1]=deck[j];

                opprank =0; //fixme Rank(oppcards, boardcards);
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
        int ourrank =0; //fixme Rank(ourcards, boardcards);


        //available cards
        HashSet<Card> cardSet=Simulator.getCardSet();
        ourcards.forEach(cardSet::remove);
        boardcards.forEach(cardSet::remove);
        Card[] deck=new Card[cardSet.size()];
        cardSet.toArray(deck);

        //Consider all two card combinations of the remaining cards for the opponent.
        Card[] oppcards = new Card[2];
        int opprank;

        for (int i = 0; i < deck.length-1; i++) {
            oppcards[0]=deck[i];
            for (int j =i+1; j < deck.length; j++) {
                oppcards[1]=deck[j];

                opprank =0; //fixme Rank(oppcards, boardcards);
                if (ourrank > opprank) index = ahead;
                else if (ourrank == opprank) index = tied;
                else index = behind;
                HPTotal[index] += 1;

                Card[] plusCards = new Card[2];
                // All possible board cards to come.
                for (int k=0;k<deck.length-1;k++) {//for each case(turn)
                    if(k==i|| k==j) continue;
                    plusCards[0]=deck[k];
                    for (int l=k+1;l<deck.length;l++) {//for each case(river)
                        if(l==i|| l==j) continue;
                        plusCards[1]=deck[l];

                        int ourbest =0; //fixme Rank(ourcards, boardcards,plusCards);
                        int oppbest =0; //fixme Rank(oppcards, boardcards,plusCards);

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
