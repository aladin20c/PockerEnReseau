package Game;



public class TexasHoldem extends PokerGame{


    private Hand handOfTable;
    private boolean smallBlind = false; //cet attribut sera à vrai lorsque le joueur aprés le dealer mise la smallBilnd
    private boolean bigBlind = false; ////cet attribut sera à vrai lorsque le 2éme joueur aprés le dealer mise la bigBilnd



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
        return bidTurn>4 || players.size()-foldedPlayers>1;
    }

    @Override
    public boolean isTurnFinished(){
        if(bidTurn==0 ){
            return pot==minBid+minBid/2;
        }else {
            for (Player p : players) {
                if (!p.hasFolded()) {
                    if (!p.played || p.bidPerRound != bidAmount) {
                        return false;
                    }
                }
            }
            return true;
        }
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
        if(player==getCurrentPlayer() && bidTurn!=0){
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
                return (player.getBidPerRound() == bidAmount);
            }
        }
        return false;
    }


    @Override
    public boolean canRaise(Player player,int raiseAmount){////fixme gives error when first player pay small blind and quits
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
}
