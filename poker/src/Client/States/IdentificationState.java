package Client.States;

import Client.Client;
import Game.utils.Request;

public class IdentificationState extends GameState{


    public IdentificationState(Client client) {
        super(client);
    }


    @Override
    public void analyseMessageToSend(String messageToSend) {
    }
    @Override
    public void analyseComingMessage(String comingMessage) {
        if(comingMessage.matches(Request.WELCOME)){

            String name=comingMessage.substring(12);
            this.client.setGameState(new MenuState(client,name));

        }else if(comingMessage.matches(Request.STATE)){

            if(!comingMessage.equals("666 IdentificationState")) throw new RuntimeException("states not synchronized between server and client found "+comingMessage+" required IdentificationState");

        }
    }
}
