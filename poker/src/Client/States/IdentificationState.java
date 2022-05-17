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
        if(comingMessage.matches(Request.LARGE_NAME)){
            if(client.getClientFrame() != null) {
                client.getClientFrame().setLargeName(true);
            }
        }
        else{
            if(comingMessage.matches(Request.USED_NAME)){
                if(client.getClientFrame() != null) {
                    client.getClientFrame().setUsedName(true);
                }
            }
            else{
                if(comingMessage.matches(Request.WELCOME)){
                    if(client.getClientFrame() != null) {
                        client.getClientFrame().setWelcome(true);
                    }
                    String name=comingMessage.substring(12);
                    this.client.setGameState(new MenuState(client,name));
                }else if(comingMessage.matches(Request.STATE)){

                    if(!comingMessage.equals("666 IdentificationState")) throw new RuntimeException("states not synchronized between server and client found "+comingMessage+" required IdentificationState");

                }
            }
        }
    }
}
