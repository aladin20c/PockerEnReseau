package Game;
import java.util.ArrayList;
import java.util.Scanner;
public abstract class PokerGame {
    protected ArrayList<Player> players;
    protected int dealer;
    protected Player winner;
    protected Deck deck;
    protected int bidAmount=0;
    protected int pot=0;
    protected int totalCheck=0;
    protected int foldedPlayers=0;
    protected boolean isOneTurnCompleted=false;
    protected boolean isGameFinished=false;
    protected Scanner sc;
 
    public PokerGame(ArrayList<Player> players){
        deck = new Deck();
        this.players=players;
        dealer=0;
        initPlayer();
        sc = new Scanner(System.in);
    }
 
    /**
     * To get the deck
     * @return
    */
    public Deck getDeck(){
        return deck;
    }
    
    /**
     * To get the index of the next player
     * @return
     */
     public int nextPlayer(int i){
         int n=(i+1)%players.size();
         if(!players.get(n).isFold()){
             return n;
         }
         else{
             return nextPlayer(n);
         }
     }
 
     /**
     * To get the bidAmount
     * @return
     */
     public int getBidAmount(){
         return bidAmount;
     }
 
     /**
      * To set bidAmount
      * @param bidAmount
      */
     public void setBidAmount(int bidAmount) {
         this.bidAmount = bidAmount;
     }
 
     /**
      * To get pot
      * @return
      */
     public int getPot() {
         return pot;
     }
 
     public void initPlayer(){
         for(Player p : players){
             p.setRound(this);
         }
     }
 
     /**
      * distribute nbCard to all players (no folded players)
      * @param nbCard
      */
     public void distributeCard(int nbCard){
         int nbPlayers = players.size()-foldedPlayers;
         Hand[] hands = new Hand[nbPlayers];
         int index = dealer;
         for(int i=0 ; i<nbPlayers ; i++){
             index=nextPlayer(index);
             hands[i]=players.get(index).getHand();
         }
         deck.dealPlayers(hands, nbCard);
     }
 
     public void makeBid(String betType, int raiseAmount, int callAmount){
         switch(betType){
             case "CALL":
                 call(callAmount);
                 break;
             case "RAISE":
                 raise(raiseAmount,callAmount);
                 break;
             default :
                 //Lancer une exception
                 System.out.println("Cette action n'existe pas");
         }
     }
 
     public void makeBid(String betType) {
         switch(betType){
             case "CHECK":
                 check();
                break;
             case "FOLD":
                 fold();
                break;
             default :
              //Lancer une exception
              System.out.println("Cette action n'existe pas");
         }
     }
 
     public void fold(){
         foldedPlayers++;
     }
 
     public void check(){
         totalCheck++;
 
     }
 
     public void call(int callAmount){
         pot  += callAmount;
         check();
 
     }
 
     public void raise(int raiseAmount,int callAmount){
         bidAmount += raiseAmount;
         pot = pot + raiseAmount+callAmount;
         totalCheck = 1;
 
     }
 
     public void resetBidTurn() {
          
     }
     
     public void biddingRound(int firstPlayer , boolean firstRound){
         int index = firstPlayer;
         Player player = players.get(index);
         resetBidPerRoundOfPlayers();
         if(!firstRound){
             bidAmount=0;
         }
         totalCheck=0;
         while(foldedPlayers<players.size()-1 && totalCheck<(players.size()-foldedPlayers) ){
             System.out.println("Le joueur "+player.getName()+" doit choisir :");
             actionOfBiddingRound();
             String rep = sc.next();
             switch(rep){
                 case "0" : 
                     player.call();
                     break;
                 case "1":
                     player.fold();
                     if(index==dealer){
                         dealer = nextPlayer(dealer);
                     }
                     break;
                 case "2" :
                     player.check();
                     break;
                 case "3":
                     System.out.println("Entrez une somme pour faire un raise");
                     int raiseAmount = sc.nextInt();
                     player.raise(raiseAmount);
                     break;
                 default:
                     System.out.println("Vous devez choisir une de ces options");
                     actionOfBiddingRound();
             }
             index = nextPlayer(index);
             player = players.get(index);
 
         }
     }
     public void actionOfBiddingRound(){
         System.out.println("0- CALL");
         System.out.println("1- FOLD");
         System.out.println("2- CHECK");
         System.out.println("3- RAISE");
         System.out.println("Entrez votre choix : ");
     }
     public void resetBidPerRoundOfPlayers(){
         for(Player p:players){
             p.setBidPerRound(0);
         }
     }

    public Player getWinner() {
        return winner;
    }
    public void defineWinner(){
         for(Player p:players){
            if(p.getHand().compareTo(this.winner.getHand())==1){
                winner=p;
            }
         }
    }

    public static Player winner(ArrayList<Player> players){
         Player winner=players.get(0);
         for(int i=1;i<players.size();i++){
             if(players.get(i).getHand().compareTo(winner.getHand())==1){
                 winner=players.get(i);
             }
         }
         return winner;
    }
    public abstract boolean isGameTurnFinished();
     public abstract void playGame();
     public abstract boolean checkEndOfTurn();
}
