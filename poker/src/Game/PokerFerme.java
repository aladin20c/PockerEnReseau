package Game;

import java.util.ArrayList;
public class PokerFerme extends PokerGame{

    public PokerFerme( int minBid){
        super(minBid);
    } 
    
    @Override
    public boolean isRoundFinished() {
        return bidTurn==4;
    }
    @Override
    public boolean can_reset_game(){
        int nbPlayers=0;
        for(Player p:players){
            if(!p.isQuit()){
                nbPlayers++;
            }
        }
        return nbPlayers>=3 && nbPlayers()<=8;
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
            if(bidTurn==0 && raiseAmout==minBid){
                return true;
            }
            if(bidTurn!=2){
                int callAmount = bidAmount-player.getBidPerRound();
                return ((raiseAmount>bidAmount)&&((callAmount)<=player.getStack()));
            }
        }
        return false;
    }
    @Override
    public boolean canChange(Player player,ArrayList<Card> cards){
        return (cards.size()>=1
                && cards.size()<5
                && player.getHand().getCards().containsAll(cards)
                && bidTurn==2
                && players.get(currentPlayer)==player);

    }
    @Override
    public ArrayList<Card> change(Player player,ArrayList<Card> cards){
        deck.getCards().addAll(cards);
        ArrayList<Card> newCards=new ArrayList<>();
        for(int i=0 ;i<cards.size();i++){
            newCards.add(deck.getNextCard());
        }
        player.getHand().getCards().addAll(newCards);
        return newCards;
    }

}