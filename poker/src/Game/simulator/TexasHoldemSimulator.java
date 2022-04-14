package Game.simulator;

import Game.Card;
import Game.Hand;

import java.util.ArrayList;
import java.util.HashSet;

public class TexasHoldemSimulator extends Simulator{






    /*naive simulation for texas holdem
    we assume that every player already got 2 cards*/
    public void simulate(Hand playerhand, Hand tablehand, int numeberOfPlayers){

        //preparing necessary cards for simulation
        HashSet<Card> cardSet=getCardSet();
        cardSet.removeAll(tablehand.getCards());
        cardSet.removeAll(playerhand.getCards());

        //preparing simulation variables
        int ahead = 0;
        int tied=0;
        int behind = 0;
        boolean isahead=false;
        boolean istied=false;
        boolean isbehind=false;

        ArrayList<Card> ourhand=new ArrayList<>();
        ourhand.addAll(playerhand.getCards());
        ArrayList<Card>[] opphands = new ArrayList[numeberOfPlayers-1];
        for (int i = 0; i < numeberOfPlayers; i++) {
            opphands[i] = new ArrayList<>();
            opphands[i].add(null);
            opphands[i].add(null);
        }
        ArrayList<Card> boardhand=new ArrayList<>();
        ArrayList<Card> deck;

        int ourrank;
        int opprank;

        //simulation
        for(int i=0;i<100000;i++){

            deck = new ArrayList<>(cardSet);
            boardhand.clear();
            boardhand.addAll(tablehand.getCards());
            deck.remove(0);
            distributeCards(deck,opphands,2);

            isahead=false;
            istied=false;
            isbehind=false;

            deck.remove(0);
            int n=boardhand.size();
            if(n<3){
                boardhand.add(deck.remove(0));
                boardhand.add(deck.remove(0));
                boardhand.add(deck.remove(0));
            }else if(n==3 ){
                boardhand.add(deck.remove(0));
                boardhand.add(deck.remove(0));
            }else if (n==4){
                boardhand.add(deck.remove(0));
            }


            //ranking hands
            ourrank=Rank(ourhand,boardhand);
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
            }else{
                ahead++;
            }
        }

        System.out.println("ahead="+ahead);
        System.out.println("tied="+tied);
        System.out.println("behind="+behind);
    }



    
}
