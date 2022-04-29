package Game;
import Client.Client;



public class AI {

    private final static long TIME_TO_PLAY = 5000; //ms
    private final Client client;


    public AI(Client client) {
        this.client = client;
    }


    /**
     * determines which action the AI should do
     */
    public void whichAction(Player player, PokerGame round, double proba) {
        try {
            Thread.sleep(TIME_TO_PLAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        double RR = rateOfReturn(player, round, proba);
        if (RR < 0.8) {
            this.client.writeToServer("410 FOLD");
            System.out.println("IA FOLD");
        }
        if (RR < 1.0) {
            this.client.writeToServer("410 FOLD");
            System.out.println("IA FOLD");

        }
        if (RR < 1.3) {
            this.client.writeToServer("412 CALL");
            System.out.println("IA CALL");

        } else {
            double somme = shouldBet(proba);
            this.client.writeToServer("413 RAISE" + (int) somme + "");
            System.out.println("IA RISE");

        }
        /*
        Calculer le RateOfReturn puis décider

            If RR < 0.8 then 95% fold, 0 % call, 5% raise (bluff) -> FOLD
            If RR < 1.0 then 80%, fold 5% call, 15% raise (bluff) -> FOLD
            If RR <1.3 the 0% fold, 60% call, 40% raise -> CALL
            Else (RR >= 1.3) 0% fold, 30% call, 70% raise -> RAISE
            If fold and amount to call is zero, then call. -> CALL

         */

    }


    /*
        RateOfReturn == RR == Hand Strenght / Pot Odds

        The pot odds number is the ratio of your bet or call to the size of
        the pot after you bet (the amount you will win).
        For example, if the bet is $20, and there is $40 in the pot,
        then the pot odds are 20/(20+40) = 0.333.
 */
    public double rateOfReturn(Player player, PokerGame round, double hendStrenght) {
        int potOdds = (player.getBidPerRound()) / (round.pot + player.getBidPerRound());
        return hendStrenght / potOdds;
    }


    /**
     * Compute a percentage to bet with the change of win
     */
    public double shouldBet(/*Player player, PokerGame round,*/double win) {
        double a = Math.pow(Math.E, win);
        double b = (1.3 - win);
        double c = Math.E / 0.3;
        double res = a / b / c;
        return res;
    }

    /*

       int minBet = dealer.getMinBet();
       int toBet = -1; //default to bet is fold

       int shouldBet = hasMultiple(river) + hasFlush(river) + hasStraight(river);
       if (shouldBet <= 10 && shouldBet > 0) {
           toBet = minBet; //if low chances, call or check
       } else if (shouldBet <= 20 && shouldBet > 10) {
           toBet = (int) (this.getCash() * .02);
       } else if (shouldBet <= 30 && shouldBet > 20) {
           toBet = (int) (this.getCash() * .04);
       } else if (shouldBet > 30) {
           toBet = (int) (0.10 * this.getCash());
       }

       if (toBet <= dealer.getCurrentBet() / 4) {
           toBet = 0;
       }

       return toBet;

        */

   public static void main(String[] args) {

        AI ai = new AI(null);
        System.out.println(ai.shouldBet(0.1));
    }
}

