package Game;

import java.util.ArrayList;

public class TexasHoldem extends PokerGame{
    private Hand handOfTable;
    private boolean smallBlind = false; //cet attribut sera à vrai lorsque le joueur aprés le dealer mise la smallBilnd
    private boolean bigBlind = false; ////cet attribut sera à vrai lorsque le 2éme joueur aprés le dealer mise la bigBilnd

    public TexasHoldem(ArrayList<Player> players, int minBid){
        super(players);
        super.minBid=minBid;
    }

    public void rotate(){
        if(isTurnFinished()){
            bidTurn++;
            currentPlayer=nextPlayer(dealer);
            for(Player p : players){
                p.setPlayed(false);
            }
            switch (bidTurn){
                case 2:
                    deck.burn();
                    deck.deal(handOfTable,3);
                    break;
                case 3:
                    deck.burn();
                    deck.deal(handOfTable,1);
                    break;
                case 4:
                    deck.burn();
                    deck.deal(handOfTable,1);
                    break;
            }
        }
        else{
            if(smallBlind && bigBlind){
                bidTurn++;
                players.get(nextPlayer(dealer)).setPlayed(false);
                players.get(nextPlayer(nextPlayer(dealer))).setPlayed(false);
                currentPlayer=nextPlayer(dealer);
            }
            else{
                currentPlayer().setPlayed(true);
                currentPlayer=nextPlayer(currentPlayer);
            }
        }
    }
    @Override
    public boolean isRoundFinished() {
        return bidTurn==5;
    }
    @Override
    public boolean can_reset_game(){
        int nbPlayers=0;
        for(Player p:players){
            if(!p.isQuit()){
                nbPlayers++;
            }
        }
        return  nbPlayers()>=2 &&  nbPlayers()<=10;
    }
    @Override
    public boolean canCall(Player player){
        if(player==getCurrentPlayer()){
            return ((player.getBidPerRound()<bidAmount)&&((bidAmount-player.getBidPerRound())<=player.getStack()));
        }
        return false;
    }
    @Override
    public boolean canFold(Player player){
        return player==getCurrentPlayer();
    }
    @Override
    public boolean canCheck(Player player){
        if(bidTurn!=0){
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
                return ((raiseAmount>bidAmount)&&((callAmount)<=player.getStack()));
            }
        }
        return false;
    }
}