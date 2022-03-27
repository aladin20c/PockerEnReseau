package Game;

import java.util.ArrayList;
public class PokerFerme extends PokerGame{

    public PokerFerme(ArrayList<Player> players, int bidAmount){
        super(players);
        super.bidAmount=bidAmount;
    }
    public void rotate(){
        if(isTurnFinished()){
            bidTurn++;
            bidAmount=0;
            resetBidPerRoundOfPlayers();
            currentPlayer=nextPlayer(dealer);
        }
        else{
            currentPlayer=nextPlayer(currentPlayer);
        }
    }
    public boolean isGameTurnFinished(){
        return bidTurn==2;
    }

    public void discard(){
        int nbPlayers = players.size()-foldedPlayers;
        int index = dealer;
        for(int i=0 ; i<nbPlayers ; i++){
            index=nextPlayer(index);
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
    


}