package Game;

import java.util.ArrayList;

public class TexasHoldem extends PokerGame{
    private Hand handOfTable;
    private boolean smallBlind = false; //cet attribut sera à vrai lorsque le joueur aprés le dealer mise la smallBilnd
    private boolean bigBlind = false; ////cet attribut sera à vrai lorsque le 2éme joueur aprés le dealer mise la bigBilnd

    public TexasHoldem(ArrayList<Player> players, int bidAmount){
        super(players);
        super.bidAmount=bidAmount;
    }
    public void rotate(){
        if(isTurnFinished()){
            bidTurn++;
            bidAmount=0;
            resetBidPerRoundOfPlayers();
            currentPlayer=nextPlayer(dealer);
            switch (bidTurn){
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
            }
        }
        else{
            currentPlayer=nextPlayer(currentPlayer);
        }
    }
    @Override
    public boolean isRoundFinished() {
        return bidTurn==3;
    }
    @Override
    public boolean can_reset_game(){
        return players.size()>=2 && players.size()<=10;
    }
    @Override
    public boolean canCall(Player player){
        if(player==getCurrentPlayer()){
            if(bidTurn==0 && smallBlind && bigBlind){
                return ((player.getBidPerRound()<bidAmount)&&((bidAmount-player.getBidPerRound())<=player.getStack()));
            }
        }
        return false;
    }
    @Override
    public boolean canFold(Player player){
        return player==getCurrentPlayer();
    }
    @Override
    public boolean canCheck(Player player){
        if(bidTurn==1){
            if(player==getCurrentPlayer()){
                return (player.getBidPerRound == bidAmount);
            }
        }
        return false;
    }
    @Override
    public boolean canRaise(Player player,int raiseAmount){
        if(player==getCurrentPlayer()){
            if(bidTurn==0){
                if(currentPlayer==nextPlayer(dealer)){
                    if(raiseAmount==bidAmount/2 && raiseAmount <=player.getStack()){
                        smallBlind=true;
                        return true;
                    }
                }
                if(currentPlayer==nextPlayer(nextPlayer(dealer))){
                    if(raiseAmount==bidAmount && raiseAmount <=player.getStack()){
                        bigBlind=true;
                        return true;
                    }
                }
            }
            else{
                int callAmount = bidAmount-player.getBidPerRound();
                return ((player.getBidPerRound()<bidAmount)&&((bidAmount- callAmount-raiseAmount)<=player.getStack()));
            }
        }
        return false;
    }
}