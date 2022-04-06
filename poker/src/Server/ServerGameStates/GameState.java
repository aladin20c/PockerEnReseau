package Server.ServerGameStates;


import Server.ClientHandler;
import Server.Room;


public abstract class GameState {

    protected ClientHandler clientHandler;
    protected Room room;

    public GameState(ClientHandler clientHandler,Room room) {
        this.clientHandler = clientHandler;
        this.room=room;
    }
    public GameState(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        this.room=null;
    }

    public void writeToClient(String message){
        clientHandler.writeToClient(message);
    }

    public void broadCastMessage(String message){
        clientHandler.broadCastMessage(message,room.getClientHandlers());
    }

    public void broadCastMessageToEveryone(String message){
        clientHandler.broadCastMessageToEveryone(message,room.getClientHandlers());
    }

    public abstract void analyseRequest(String messageFromClient);

    public void clientQuit(){}

    public int getResponse(){
        return 0;
    }
}


