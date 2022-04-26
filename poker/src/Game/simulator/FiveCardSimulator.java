package Game.simulator;


import Game.Card;
import Game.Hand;
import Game.Player;

import java.util.*;

public class FiveCardSimulator implements Simulator{

    public void simulate(){

    }



    /*simulation for first betting round five card poker we assume that dealer is the first in the list*/
    public static void simulate(Player ourPlayer, ArrayList<Player> players){
        if(ourPlayer==null || players==null || players.isEmpty()) return;

        //preparing necessary cards for simulation
        HashSet<Card> cardSet=Simulator.getCardSet();
        ourPlayer.getHand().getCards().forEach(cardSet::remove);
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

            for (int cnt=0;cnt<5;cnt++){
                for (Map.Entry<Player, Hand> entry : oppHands.entrySet()) {
                    entry.getValue().add(deck.remove(0));
                }
            }

            //ranking hands
            boolean isahead=false;
            boolean istied=false;
            boolean isbehind=false;
            int ourrank=Simulator.Rank(ourHand);
            int opprank;

            for (Map.Entry<Player, Hand> entry : oppHands.entrySet()) {
                if(!entry.getKey().hasFolded()){
                    opprank=Simulator.Rank(entry.getValue());
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

            if(isahead){
                ahead++;
            }else if(istied){
                tied++;
            }else if(isbehind){
                behind++;
            }
        }

        System.out.println("ahead="+(double)ahead/10000);
        System.out.println("tied="+(double)tied/10000);
        System.out.println("behind="+(double)behind/10000);
    }



    /*simulation for second betting round five card poker we assume that dealer is the first in the list*/
    public static void simulate(Player ourPlayer, ArrayList<Player> players, ArrayList<ChangeEvent> events){

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


        //preparing drawing phase player hands
        ChangeEvent ourDraw=null;
        for (ChangeEvent ce : events){
            if(ce.player==ourPlayer){
                ourDraw= ce;
            }
        }

        //preparing necessary cards for simulation
        HashSet<Card> cardSet=Simulator.getCardSet();
        ArrayList<Card> ourDiscardedCardes=new ArrayList<>();
        ourPlayer.getHand().getCards().forEach(cardSet::remove);
        if (ourDraw!=null){
            for (Card c : ourDraw.discradedCards){
                cardSet.remove(c);
                ourDiscardedCardes.add(c);
            }
        }
        ArrayList<Card> deck;

        //simulation
        for(int i=0;i<1000000;i++){

            //preparing deck and hands
            deck = new ArrayList<>(cardSet);
            Collections.shuffle(deck);
            for (Map.Entry<Player, Hand> entry : oppHands.entrySet()) {
                entry.getValue().clear();
            }

            //distributing cards
            for (int cnt=0;cnt<5;cnt++){
                for (Map.Entry<Player, Hand> entry : oppHands.entrySet()) {
                    entry.getValue().add(deck.remove(0));
                }
            }

            //drawing cards
            for(ChangeEvent e : events){
                if(e.player!=ourPlayer){
                    Hand h= oppHands.get(e.player);
                    h.discardAndDrawRandomlessly(e.nbCards,deck);
                }else{
                    deck.addAll(Arrays.asList(e.discradedCards));
                }
            }

            //ranking hands
            boolean isahead=false;
            boolean istied=false;
            boolean isbehind=false;
            int ourrank=Simulator.Rank(ourHand);
            int opprank;

            for (Map.Entry<Player, Hand> entry : oppHands.entrySet()) {
                if(!entry.getKey().hasFolded()){
                    opprank=Simulator.Rank(entry.getValue());
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

        System.out.println("ahead="+(double)ahead/10000);
        System.out.println("tied="+(double)tied/10000);
        System.out.println("behind="+(double)behind/10000);
    }


    public static void main(String[] args) {
        Player player=new Player("ala",1000);
        player.getHand().add(new Card("T1"));
        player.getHand().add(new Card("C13"));
        player.getHand().add(new Card("S5"));
        player.getHand().add(new Card("S11"));
        player.getHand().add(new Card("D3"));
        ArrayList<Player> players=new ArrayList<>();
        players.add(new Player("hu",976));
        players.add(new Player("ddu",976));
        players.add(player);
        ArrayList<ChangeEvent> events=new ArrayList<>();
        events.add(new ChangeEvent(players.get(1),4));
        events.add(new ChangeEvent(players.get(0),3));
        Card[] cards1={new Card("T1"),new Card("C13")};
        Card[] cards2={new Card("C6"),new Card("D6")};
        events.add(new ChangeEvent(player,2,cards1,cards2));
        simulate(player,players);
        simulate(player,players,events);
    }


}
