package Game;

public class PokerFerme extends PokerGame{
    public PokerFerme(ArrayList<Player> players, int bidAmount){
        super(players);
        super.bidAmount=bidAmount;
    }
    
    public void firstRound(){
        int nbPlayers = players.size()-foldedPlayers;
        int index = dealer;
        for(int i=0 ; i<nbPlayers ; i++){
            index=nextPlayer(index);
            actionOfStart(); //chaque joueur doit choisir soit de faire un CALL (pour continuer à jouer) ou un FOLD (pour se coucher)
            String rep=sc.next();
            switch(rep){
                case "0" :
                    players.get(index).call();
                    break;
                case "1" :
                    players.get(index).fold();
                    if(index==dealer){
                        dealer = nextPlayer(dealer);
                    }
                    break;
                default :
                    System.out.println("Vous devez choisir une de ces options");
                    actionOfStart();
            }
            System.out.println("pot : "+pot);
            players.get(index).afficher();
        }
    }
            
    public void actionOfStart(){
        System.out.println("0- CALL");
        System.out.println("1- FOLD");
        System.out.println("Entrez votre choix : ");
    }
    
    public void discard(){
        int nbPlayers = players.size()-foldedPlayers;
        int index = dealer;
        for(int i=0 ; i<nbPlayers ; i++){
            index=nextPlayer(index);
            System.out.println("Vous voulez changer combien de cartes (entre 0 et 3)");
            int nbCards = sc.nextInt();
            if(nbCards!=0){
                for(int j=0 ; j<nbCards ; j++){
                    System.out.println("Entrez la carte : ");
                    String s = sc.next();
                    Card c = players.get(index).getHand().removeCard(s);
                    deck.add(c);
                }
            }
        }
    }   
    public void redistributeCard(){
        deck.shuffle();
        int nbPlayers = players.size()-foldedPlayers;
        int index = dealer;
        for(int i=0 ; i<nbPlayers ; i++){
            index=nextPlayer(index);
            int n=5-players.get(index).getHand().nbCards();
            for(int j=0 ; j<n ; j++){
                Card c = deck.getNextCard();
                players.get(index).getHand().add(c);
            }
        }

    }

    public void playGame(){
        deck.populate();
        firstRound();
        deck.shuffle();
        distributeCard(5);
        CardsOfPlayers();
        biddingRound(nextPlayer(dealer),false);
        discard();
        CardsOfPlayers();
        redistributeCard();
        CardsOfPlayers();
        biddingRound(nextPlayer(dealer),false);
    }
    public boolean checkEndOfTurn(){
        return false;
    }
    public boolean isGameTurnFinished(){
        return false;
    }


}