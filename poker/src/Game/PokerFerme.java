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
    @Override
    public boolean isRoundFinished() {
        return bidTurn==4; //la condition à revoir
    }
    @Override
    public boolean can_reset_game(){
        return players.size()>=3 && players.size()<=8;
    }
    @Override
    public boolean canCall(Player player){
        if(player==getCurrentPlayer()){
            if(bidTurn!=2){
                return ((player.getBidPerRound()<bidAmount)&&((bidAmount-player.getBidPerRound())<=player.getStack()));
            }
        }
        return false;
    }
    @Override
    public boolean canFold(Player player){
        return player==getCurrentPlayer() && bidTurn!=2;
    }
    @Override
    public boolean canCheck(Player player){
        if(bidTur!=0 && bidTur!=2){
            if(player==getCurrentPlayer()){
                return (player.getBidPerRound == bidAmount);
            }
        }
        return false;
    }
    @Override
    public boolean canRaise(Player player,int raiseAmount){
        if(player==getCurrentPlayer()){
            if(bidTurn!=2){
                int callAmount = bidAmount-player.getBidPerRound();
                return ((player.getBidPerRound()<bidAmount)&&((bidAmount- callAmount-raiseAmount)<=player.getStack()));
            }
        }
        return false;
    }

}