package Game;

import java.util.ArrayList;

public class TexasHoldem extends PokerGame{
    private Hand handOfTable;
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
///////////////////////////////////////TODO
    @Override
    public boolean isRoundFinished() {
        return false;
    }
//////////////////////////////
    public boolean isGameTurnFinished(){
        return bidTurn==3;
    }
    public boolean can_reset_game(){
        return players.size()>=2 && players.size()<=10;
    }
}