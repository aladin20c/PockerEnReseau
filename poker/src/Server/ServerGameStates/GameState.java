package Server.ServerGameStates;

import Server.ClientHandler;

import java.util.List;

public abstract class GameState {

    protected ClientHandler clientHandler;
    protected int order;

    public GameState(ClientHandler clientHandler, int order) {
        this.clientHandler = clientHandler;
        this.order = order;
    }


    public void sendError(){
        clientHandler.writeToClient("999 ERROR");
    }
    public void writeToClient(String message){
        clientHandler.writeToClient(message);
    }
    public void broadCastMessage(String message, List<ClientHandler> clientHandlers){
        clientHandler.broadCastMessage(message,clientHandlers);
    }
    public void broadCastMessageToEveryone(String message, List<ClientHandler> clientHandlers){
        clientHandler.broadCastMessageToEveryone(message,clientHandlers);
    }



    public abstract void analyseRequest(String messageFromClient);
    public void playerQuit(){}


    public boolean canAddNewPlayer(){
        return false;
    }
    public void requestStart(boolean start){}
    public int getResponse(){
        return 0;
    }
}


