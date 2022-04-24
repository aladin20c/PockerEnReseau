package Server.ServerGameStates;


import Server.ClientHandler;
import Server.Room;

import java.io.IOException;


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

    public abstract void analyseRequest(String messageFromClient);

    public void writeToClient(String message){
        clientHandler.writeToClient(message);
    }
    public void broadCastMessage(String messageToSend){
        for (ClientHandler clientHandler : room.getClientHandlers()) {
            try {
                if (clientHandler!=this.clientHandler) {
                    clientHandler.getBufferedWriter().write(messageToSend);
                    clientHandler.getBufferedWriter().newLine();
                    clientHandler.getBufferedWriter().flush();
                }
            } catch (IOException e) {
                clientHandler.closeEverything();
            }
        }
    }
    public void broadCastMessageToEveryone(String messageToSend){
        for (ClientHandler clientHandler : room.getClientHandlers()) {
            try {
                clientHandler.getBufferedWriter().write(messageToSend);
                clientHandler.getBufferedWriter().newLine();
                clientHandler.getBufferedWriter().flush();
            } catch (IOException e) {
                clientHandler.closeEverything();
            }
        }
    }



    public void broadCastTask(String string){
        for (ClientHandler clientHandler : room.getClientHandlers()) {
            if (clientHandler!=this.clientHandler) {
                    clientHandler.addTask(string);
            }
        }
    }
    public void broadCastTaskToEveryone(String string){
        for (ClientHandler clientHandler : room.getClientHandlers()) {
            clientHandler.addTask(string);
        }
    }
    public void broadCastCancel(String string){
        for (ClientHandler clientHandler : room.getClientHandlers()) {
            if (clientHandler!=this.clientHandler) {
                clientHandler.cancelTask(string);
            }
        }
    }


    public void clientQuit(){}

    public void setStartResponse(int response){}
    public int getStartResponse() {
        return -1;
    }

    public int getEndgameResponse() {
        return -1;
    }
    public void setEndgameResponse(int endgameResponse) {

    }
}


