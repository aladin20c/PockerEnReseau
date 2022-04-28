package Game.simulator;

import Game.Card;
import Game.Hand;
import Game.Player;
import Game.TexasHoldem;


import java.util.*;
import java.util.List;

public class TexasHoldemSimulator implements Simulator{



    public static TexasHoldemSimulator.Data simulate(Player ourPlayer,TexasHoldem game){
        Data data = naive_simulation(ourPlayer, game.getTable(), game.getPlayers());
        /*int activePlayers=game.getPlayers().size()-game.getFoldedPlayers();
        Data in= HandPotential(ourPlayer.getHand().getCards(),game.getTable().getCards(),activePlayers);
        data.merge_secondry_data(in);*/
        return data;
    }


    /*naive simulation for texas holdem we assume that every player already got 2 cards*/
    public static TexasHoldemSimulator.Data naive_simulation(Player ourPlayer, Hand tablehand, ArrayList<Player> players){
        int tries=100000;
        long begin=System.nanoTime();
        if(players==null || ourPlayer==null || tablehand==null ||ourPlayer.getHand().nbCards()!=2) return new Data();
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
        return (new TexasHoldemSimulator.Data(tries,time,((double)ahead*100)/tries,((double)tied*100)/tries,((double)behind*100)/tries));
    }



    /*calculating handPotential*/
    public static TexasHoldemSimulator.Data HandPotential(List<Card> ourcards, List<Card> boardcards,int activePlayers) {
        if(ourcards==null || boardcards==null || boardcards.size()<3 || ourcards.size()!=2) return new Data();
        //Hand potential array, and behind.
        int index;
        int ahead = 0;
        int tied = 1;
        int behind = 2;
        int[][] HP = new int[3][3];//initialize to 0
        int[] HPTotal = new int[3];//initialize to 0


        //available cards
        HashSet<Card> cardSet=Simulator.getCardSet();
        ourcards.forEach(cardSet::remove);
        boardcards.forEach(cardSet::remove);
        Card[] deck=new Card[cardSet.size()];
        cardSet.toArray(deck);

        //Consider all two card combinations of the remaining cards for the opponent.
        int ourrank =Simulator.rankTexasHoldem(ourcards, boardcards);
        Card[] oppcards = new Card[2];
        int opprank;

        for (int i = 0; i < deck.length-1; i++) {
            oppcards[0]=deck[i];
            for (int j =i+1; j < deck.length; j++) {
                oppcards[1]=deck[j];

                opprank =Simulator.rankTexasHoldem(Arrays.asList(oppcards), boardcards);
                if (ourrank > opprank) index = ahead;
                else if (ourrank == opprank) index = tied;
                else index = behind;
                HPTotal[index] += 1;


                // All possible board cards to come.
                Card card_1;
                Card card_2;
                if(boardcards.size()==3) {
                    for (int k = 0; k < deck.length - 1; k++) {//for each case(turn)
                        if (k == i || k == j) continue;
                        card_1 = deck[k];
                        for (int l = k + 1; l < deck.length; l++) {//for each case(river)
                            if (l == i || l == j) continue;
                            card_2 = deck[l];

                            int ourbest = Simulator.rankTexasHoldem(ourcards, boardcards,card_1,card_2);
                            int oppbest = Simulator.rankTexasHoldem(Arrays.asList(oppcards), boardcards,card_1,card_2);

                            if (ourbest > oppbest) HP[index][ahead] += 1;
                            else if (ourbest == oppbest) HP[index][tied] += 1;
                            else HP[index][behind] += 1;
                        }
                    }
                }else if(boardcards.size()==4){
                    for (int k = 0; k < deck.length; k++) {//for each case(turn)
                        if (k == i || k == j) continue;
                        card_1 = deck[k];
                        int ourbest = Simulator.rankTexasHoldem(ourcards, boardcards,card_1,null);
                        int oppbest = Simulator.rankTexasHoldem(Arrays.asList(oppcards), boardcards,card_1,null);
                        if (ourbest > oppbest) HP[index][ahead] += 1;
                        else if (ourbest == oppbest) HP[index][tied] += 1;
                        else HP[index][behind] += 1;
                    }
                }
            }
        }

        //PPot: were behind but moved ahead
        double PPot = ((double) HP[behind][ahead] + (double) HP[behind][tied] / 2 + ((double)HP[tied][ahead]) / 2) / ((double)(HPTotal[behind] + HPTotal[tied]) / 2);
        //NPot: were ahead but fell behind.
        double NPot = (HP[ahead][behind] + (double)HP[tied][behind] / 2 + (double)HP[ahead][tied] / 2) / ((double)(HPTotal[ahead] + HPTotal[tied]) / 2);
        //HS:Hand Strength
        double HS=((double) HPTotal[ahead] + (double)HPTotal[tied] / 2) / ((double)HPTotal[ahead] + HPTotal[tied] + HPTotal[behind]);
        //EHS:Effective Hand Strength
        double HS_=Math.pow(HS,activePlayers);
        double EHS =HS_ * (1 - NPot) + (1 - HS_) * PPot;
        //EHS:Effective Hand Strength Reduit
        double EHSR =   (1 - HS_) * PPot;

        return new Data(PPot,NPot,HS,EHS,EHSR);
    }




    public static class Data{
        int tries;
        double time;
        double ahead;
        double tied;
        double behind;

        double PPot;
        double NPot;
        double HS;
        double EHS;
        double EHSR;

        public Data() {
        }

        public Data(int tries,double time, double ahead, double tied, double behind) {
            this.tries=tries;
            this.time = time;
            this.ahead = ahead;
            this.tied = tied;
            this.behind = behind;
        }

        public Data(double PPot, double NPot, double HS, double EHS, double EHSR) {
            this.PPot = PPot;
            this.NPot = NPot;
            this.HS = HS;
            this.EHS = EHS;
            this.EHSR = EHSR;
        }

        public void merge_secondry_data(Data data){
            this.PPot = data.PPot;
            this.NPot = data.NPot;
            this.HS = data.HS;
            this.EHS = data.EHS;
            this.EHSR = data.EHSR;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "tries=" + tries +
                    ", time=" + time +
                    ", ahead=" + ahead +
                    ", tied=" + tied +
                    ", behind=" + behind +
                    ", PPot=" + PPot +
                    ", NPot=" + NPot +
                    ", HS=" + HS +
                    ", EHS=" + EHS +
                    ", EHSR=" + EHSR +
                    '}';
        }
    }

}
