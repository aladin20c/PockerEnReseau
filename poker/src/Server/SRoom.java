package Server;

import Game.Room;

import java.util.ArrayList;

public class SRoom extends Room {
    protected ArrayList<ClientHandler> clientHandlers;
    protected Boolean[] startRequestResponses;

    public SRoom(int type, int minPlayers, int minBid, int initStack) {
        super(type, minPlayers,minBid, initStack);
        this.clientHandlers=new ArrayList<>();
    }

    public boolean hasRoomLeft(){return clientHandlers.size()<this.getMinPlayers();}
    public int numberOfPlayers(){return clientHandlers.size();}
    public boolean isAdmin(String userName){
        if(clientHandlers.isEmpty()) return false;
        return clientHandlers.get(0).getClientUsername().equals(userName);
    }



    public void requestStart(){
        setStartRequested(true);
        startRequestResponses=new Boolean[clientHandlers.size()];
    }
    public void abortStartRequested(){
        setStartRequested(false);
        startRequestResponses=null;
    }
    public void respond(ClientHandler c,boolean response){
        int index=this.clientHandlers.indexOf(c);
        startRequestResponses[index]=response;
    }
    public boolean allPlayersResponded(){
        for(Boolean b : startRequestResponses){
            if(b==null) return false;
        }
        return true;
    }
    public boolean startApproved(){
        for(Boolean b : startRequestResponses){
            if(!b) return false;
        }
        return true;
    }
    public ArrayList<String> getPlayersWhoSaidNo(){
        ArrayList<String> res=new ArrayList<>();
        for(int i=0;i<clientHandlers.size();i++){
            if(!startRequestResponses[i]){
                res.add(clientHandlers.get(i).getClientUsername());
            }
        }
        return res;
    }


}

