package Game.simulator;

import Game.Card;
import Game.Hand;
import Game.Player;
import Game.TexasHoldem;


import java.util.*;
import java.util.List;

public class TexasHoldemSimulator implements Simulator{



    public static Data simulate(Player ourPlayer,TexasHoldem game){
        Data data = naive_simulation(ourPlayer, game.getTable(), game.getPlayers());
        /*int activePlayers=game.getPlayers().size()-game.getFoldedPlayers();
        Data in= HandPotential(ourPlayer.getHand().getCards(),game.getTable().getCards(),activePlayers);
        data.merge_secondry_data(in);*/
        return data;
    }


    /*naive simulation for texas holdem we assume that every player already got 2 cards*/
    public static Data naive_simulation(Player ourPlayer, Hand tablehand, ArrayList<Player> players){
        int tries=100000;
        long begin=System.nanoTime();
        if(players==null || ourPlayer==null || tablehand==null ||ourPlayer.getHand().nbCards()!=2 || ourPlayer.hasFolded()) return new Data();
        int nb=tablehand.nbCards();
        if(nb!=0 && nb!=3 && nb!=4 && nb!=5) return new Data();
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
            Player p=players.get((i+1)%players.size());
            if (p!=ourPlayer){
                oppHands.put(p,new Hand());
            }
        }

        //simulation
        for(int i=0;i<tries;i++){

            deck = new ArrayList<>(cardSet);
            Collections.shuffle(deck);
            for (Map.Entry<Player, Hand> entry : oppHands.entrySet()) {
                entry.getValue().clear();
            }

            deck.remove(0);
            for (int cnt=0;cnt<2;cnt++){
                for (Map.Entry<Player, Hand> entry : oppHands.entrySet()) {
                    entry.getValue().add(deck.remove(0));
                    entry.getValue().add(deck.remove(0));
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
            int ourrank=Simulator.rankTexasHoldem(ourHand.getCards(),Arrays.asList(boardhand));
            int opprank;

            for (Map.Entry<Player, Hand> entry : oppHands.entrySet()) {
                if(!entry.getKey().hasFolded()){
                    opprank=Simulator.rankTexasHoldem(entry.getValue().getCards(),Arrays.asList(boardhand));
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
        long end=System.nanoTime();
        double time=((double)(end-begin))/1000000000;
        return (new Data(tries,time,((double)ahead*100)/tries,((double)tied*100)/tries,((double)behind*100)/tries));
    }



}
