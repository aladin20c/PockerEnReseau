package Client.States;

import Client.Client;
import Game.Utils.Request;

public class IdentificationState extends GameState{


    public IdentificationState(Client client) {
        super(client);
        System.out.println("[gameState][IdentificationState] connecting....");
    }


    @Override
    public void analyseMessageToSend(String messageToSend) {
    }
    @Override
    public void analyseComingMessage(String comingMessage) {
        if(comingMessage.matches(Request.WELCOME)){
            String name=comingMessage.substring(12);
            this.client.setGameState(new MenuState(client,name));
        }
    }
}
