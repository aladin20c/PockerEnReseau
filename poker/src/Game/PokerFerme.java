package Game;

public class PokerFerme extends PokerGame{
    public PokerFerme(ArrayList<Player> players, int bidAmount){
        super(players);
        super.bidAmount=bidAmount;
    }
    
    public void start(){
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
                    System.out.println("Entrez l'indice de la "+j+" éme carte que vous voulez faire changer : ");
                    int pos = sc.nextInt();
                    deck.add(players.get(index).getHand().getCard(pos));
                    players.get(index).removeCard(pos);
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
            for(int j=0 ; j<(5-players.get(index).getHand().nbCards()) ; j++){
                Card c = deck.getNextCard();
                players.get(index).getHand().add(c);
            }
        }

    }

    public void playGame(){
        start();
        deck.shuffle();
        distributeCard(5);
        biddingRound();
        discard();
        redistributeCard();
        biddingRound();
    }
    public boolean checkEndOfTurn(){
        return false;
    }
    public boolean isGameTurnFinished(){
        return false;
    }
}