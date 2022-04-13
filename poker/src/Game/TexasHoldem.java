package Game;



public class TexasHoldem extends PokerGame{


    private Hand handOfTable;

    public TexasHoldem(int id, int type, int maxPlayers, int minBid, int initStack) {
        super(id, type, maxPlayers, minBid, initStack);
        handOfTable=new Hand();
    }

    public TexasHoldem(int type, int maxPlayers, int minBid, int initStack) {
        super(type, maxPlayers, minBid, initStack);
        handOfTable=new Hand();
    }

    @Override
    public boolean isRoundFinished() {
        return bidTurn>3 || players.size()-foldedPlayers<=1;
    }


    @Override
    public boolean canResetGame(){
        int nbPlayers=0;
        for(Player p:players){
            if(!p.hasQuitted()){
                nbPlayers++;
            }
        }
        return  nbPlayers>=2 &&  nbPlayers<=10;
    }

    public boolean canStartGame(){
        return  players.size()>=2 &&  players.size()<=10;
    }


    @Override
    public boolean canCall(Player player){
        if(player==getCurrentPlayer() && bidTurn<4){
            return ((player.getBidPerRound()<bidAmount)&&((bidAmount-player.getBidPerRound())<=player.getStack()));
        }
        return false;
    }

    @Override
    public boolean canFold(Player player){
        return bidTurn<4 && player==getCurrentPlayer();
    }

    @Override
    public boolean canCheck(Player player){
        if(player==getCurrentPlayer() && bidTurn<4){
            return (player.getBidPerRound() == bidAmount);
        }
        return false;
    }


    public boolean canRaise(Player player,int raiseAmount){
        if(player==getCurrentPlayer() && bidTurn<4){
            int callAmount = bidAmount-player.getBidPerRound();
            return ((raiseAmount>bidAmount)&&((callAmount)<=player.getStack()));
        }
        return false;
    }

    @Override
    public Card[] revealCards(int nbcards) {
        Card[] cards=new Card[nbcards];
        for (int i=0;i<nbcards;i++){
            cards[i]=deck.getNextCard();
        }
        handOfTable.addAll(cards);
        return cards;
    }

    @Override
    public Hand getTable(){return handOfTable;}

    @Override
    public void resetGame() {
        super.resetGame();
        handOfTable.clear();
    }

    public void fixSmallBigBlind(){
        players.get(nextPlayer(dealer)).raise(this,minBid/2);
        players.get(nextPlayer(dealer)).setPlayed(false);
        players.get(nextPlayer(nextPlayer(dealer))).raise(this,minBid);
        players.get(nextPlayer(nextPlayer(dealer))).setPlayed(false);
    }
}
