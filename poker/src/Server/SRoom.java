package Server;

import Client.Player;
import Game.Card;
import Game.Room;

import java.util.ArrayList;

public class SRoom extends Room {

    private static int COUNT=1;
    protected ArrayList<ClientHandler> clientHandlers;


    public SRoom(int type, int minPlayers, int minBid, int initStack) {
        super(COUNT++,type, minPlayers,minBid, initStack);
        this.clientHandlers=new ArrayList<>();
    }


    public void addClient(ClientHandler clientHandler){
        this.addPlayer(clientHandler.getClientUsername());
        clientHandlers.add(clientHandler);
    }
    public void removeClient(ClientHandler clientHandler){
        this.removePlayer(clientHandler.getClientUsername());
        clientHandlers.remove(clientHandler);
    }
    public boolean canAddNewClient(){
        return clientHandlers.size()<this.getMaxPlayers() && clientHandlers.get(0).getGameState().canAddNewPlayer();
    }
    public boolean hasEnoughPlayersToStart(){
        return (getType()==0 && clientHandlers.size()>=3) || (getType()==1 && clientHandlers.size()>=2);
    }
    public void requestStart(boolean start){
        for(ClientHandler ch : clientHandlers){
            ch.getGameState().requestStart(start);
        }
    }




    public boolean isAdmin(ClientHandler clientHandler){
        return clientHandlers.get(0)==clientHandler;
    }
    public ArrayList<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }
    public int numberOfClients(){return clientHandlers.size();}


}
