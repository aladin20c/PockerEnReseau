package Server;

import Game.Room;

import java.util.ArrayList;

public class SRoom extends Room {
    private static int COUNT=1;

    protected ArrayList<ClientHandler> clientHandlers;
    private ClientHandler currentPlayer;

    public SRoom(int type, int minPlayers, int minBid, int initStack) {
        super(COUNT++,type, minPlayers,minBid, initStack);
        this.clientHandlers=new ArrayList<>();
    }



    public void addClient(ClientHandler clientHandler){
        clientHandlers.add(clientHandler);
    }
    public void removeClient(ClientHandler clientHandler){
        clientHandlers.remove(clientHandler);
    }
    public boolean canAddNewPlayer(){
        if(!clientHandlers.isEmpty()){
            return clientHandlers.size()<this.getMaxPlayers() && clientHandlers.get(0).getGameState().canAddNewPlayer();
        }
        return clientHandlers.size()<this.getMaxPlayers();
    }
    public ArrayList<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }
    public boolean isAdmin(ClientHandler clientHandler){
        if(clientHandlers.isEmpty()) return false;
        return clientHandlers.get(0)==clientHandler;
    }
    public boolean hasEnoughPlayersToStart(){
        return (getType()==0 && clientHandlers.size()>=3) || (getType()==1 && clientHandlers.size()>=2);
    }

    public int numberOfPlayers(){return clientHandlers.size();}

    public void requestStart(boolean start){
        for(ClientHandler ch : clientHandlers){
            ch.getGameState().requestStart(start);
        }
    }



}

