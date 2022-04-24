package Server.ServerGameStates;

import Game.utils.Request;
import Server.ClientHandler;
import Server.Server;


public class IdentificationState extends GameState{

    public IdentificationState(ClientHandler clientHandler) {
        super(clientHandler);
    }


    @Override
    public void analyseRequest(String messageFromClient) {

        if(messageFromClient.matches(Request.JOIN) ){

            String name=messageFromClient.substring(17);
            //checking the name length
            if(name.length()>30){
                writeToClient(Request.LARGE_NAME);
                // checking if the name already exists
            }else if(Server.containsName(name)){
                writeToClient(Request.USED_NAME);
            }else if(name.matches("\\d.*") || name.contains(" ")){
                writeToClient("955 wrong format");
            }else{
                clientHandler.setClientUsername(name);
                Server.addClient(clientHandler);
                writeToClient("101 WELCOME "+name);
                System.out.println(name+" has successfully connected");
                clientHandler.setGameState(new MenuState(clientHandler));
            }

        }else if(messageFromClient.matches(Request.GET_STATE)) {

            writeToClient("666 IdentificationState");

        }else {

            clientHandler.writeToClient(Request.ERROR);

        }
    }
}
