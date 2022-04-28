package Server;


import Game.PokerGame;
import Server.ServerGameStates.Playing5CardPokerState;
import Server.ServerGameStates.PlayingTexasHoldemState;
import Server.ServerGameStates.WaitingState;
import java.util.ArrayList;


public class Room  {

    protected PokerGame game;
    protected ArrayList<ClientHandler> clientHandlers;
    private int turn;
    private boolean endgame;


    public Room() {
        this.clientHandlers=new ArrayList<>();
        this.turn=-1;
        this.endgame=false;
    }


    public void addClient(ClientHandler clientHandler){
        clientHandlers.add(clientHandler);
        game.addPlayer(clientHandler.getClientUsername());
    }
    public void removeClient(ClientHandler clientHandler){
        clientHandlers.remove(clientHandler);
    }

    public ClientHandler getClientHandler(String username){
        for(ClientHandler ch : clientHandlers){
            if(ch.getClientUsername().equals(username)) return ch;
        }
        return null;
    }


    public boolean canAddNewClient(){
        if(clientHandlers.get(0).getGameState() instanceof WaitingState){
            return !((WaitingState)clientHandlers.get(0).getGameState()).startRequested() && clientHandlers.size()<game.getMaxPlayers();
        }else {
            return false;
        }
    }

    public void requestStart(boolean start){
        for(ClientHandler ch : clientHandlers){
            if(ch.getGameState() instanceof WaitingState) {
                ((WaitingState) ch.getGameState()).setStartRequested(start);
                ch.getGameState().setStartResponse(0);
            }
        }
    }





    public void resetGame(){
        clientHandlers.add(clientHandlers.remove(0));
        ArrayList<ClientHandler> reClientHandlers=new ArrayList<>();
        for(ClientHandler ch:clientHandlers){
            if(!game.getPlayer(ch.getClientUsername()).canReplay(game) || ch.getGameState().getEndgameResponse()!=1){
                reClientHandlers.add(ch);
            }
        }
        for (ClientHandler ch:reClientHandlers){
            ch.getGameState().clientQuit();
        }

        if(game.canResetGame()){
            this.endgame=false;
            this.turn=-1;
            game.resetGame();
            for(ClientHandler ch : clientHandlers){
                ch.getGameState().setEndgameResponse(0);
            }
            if(game.getType()==1){
               ((PlayingTexasHoldemState) clientHandlers.get(0).getGameState()).rotateTurn();
            }else {
                ((Playing5CardPokerState) clientHandlers.get(0).getGameState()).rotateTurn();
            }
        }
    }


    public String informationToString(int index){
        return "121 MESS "+index+" ID "+game.getId()+" "+game.getType()+" "+game.getMaxPlayers()+" "+game.getMinBid()+" "+game.getInitStack()+" "+clientHandlers.size();
    }
    public boolean isAdmin(ClientHandler clientHandler){
        return clientHandlers.get(0)==clientHandler;
    }
    public ArrayList<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }
    public int numberOfClients(){
        return clientHandlers.size();
    }
    public boolean isEmpty(){
        return clientHandlers.isEmpty();
    }
    public PokerGame getGame() {
        return game;
    }
    public void setGame(PokerGame game) {
        this.game = game;
    }
    public int getTurn() {
        return turn;
    }
    public boolean isEndgame() {
        return endgame;
    }
    public void setEndgame(boolean endgame) {
        this.endgame = endgame;
    }
    public boolean turnIsUpToDate(){
        return turn==game.getBidTurn();
    }
    public void updateTurn(){
        this.turn=game.getBidTurn();
    }

}
