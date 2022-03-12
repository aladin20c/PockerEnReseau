package Client.States;

import Client.Client;


public abstract class GameState {

    protected Client client;
    protected int order;

    public GameState(Client client, int order) {
        this.client = client;
        this.order = order;
    }

    public void writeToServer(String messageToSend){
        this.client.writeToServer(messageToSend);
    }

    public void quit(){}

    public abstract void analyseMessageToSend(String messageToSend);
    public abstract void analyseComingMessage(String comingMessage);

}
