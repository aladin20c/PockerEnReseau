package Server;

import Game.Card;
import Game.PokerGame;
import Server.ServerGameStates.WaitingState;
import java.util.ArrayList;


public class Room  {

    protected PokerGame game;
    protected ArrayList<ClientHandler> clientHandlers;
    private int turn;

    public Room() {
        this.clientHandlers=new ArrayList<>();
        this.turn=-1;
    }


    public void addClient(ClientHandler clientHandler){
        clientHandlers.add(clientHandler);
        game.addPlayer(clientHandler.getClientUsername());
    }

    public void removeClient(ClientHandler clientHandler){
        clientHandlers.remove(clientHandler);
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
            ((WaitingState)ch.getGameState()).setStartRequested(start);
            ((WaitingState)ch.getGameState()).setResponse(-1);
        }
    }

    public ClientHandler getClientHandler(String username){
        for(ClientHandler ch : clientHandlers){
            if(ch.getClientUsername().equals(username)) return ch;
        }
        throw new RuntimeException("client handler not found");
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
    public int numberOfClients(){return clientHandlers.size();}
    public PokerGame getGame() {return game;}
    public void setGame(PokerGame game) {this.game = game;}

    public int getTurn() {return turn;}
    public void setTurn(int turn) {this.turn = turn;}

    public boolean turnIsUpToDate(){
        return turn==game.getBidTurn();
    }
    public void updateTurn(){
        this.turn=game.getBidTurn();
    }
}
