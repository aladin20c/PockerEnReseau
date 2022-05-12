package Client.States;

import Client.Client;


public abstract class GameState {

    protected Client client;
    protected boolean gameStarted;

    public GameState(Client client) {
        this.client = client;
    }

    public void writeToServer(String messageToSend){
        this.client.writeToServer(messageToSend);
    }

    public void quit(){}

    public abstract void analyseMessageToSend(String messageToSend);
    public abstract void analyseComingMessage(String comingMessage);

    public boolean isGameStarted() {
        return gameStarted;
    }

    public boolean isEndgame() {
        return false;
    }
}
