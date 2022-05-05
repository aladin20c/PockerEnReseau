package Client.States;

import Client.Client;
import Game.Player;
import Game.PokerGame;
import Game.utils.Request;


public class WaitingState extends GameState{

    private final String username;
    private PokerGame currentGame;
    private boolean startRequested;


    public WaitingState(Client client, String username, PokerGame currentGame) {
        super(client);
        this.username = username;
        this.currentGame = currentGame;
        this.startRequested=false;
    }


    @Override
    public void analyseMessageToSend(String messageToSend) {
        if(startRequested && !messageToSend.matches(Request.START_RESPONSE)){
            System.out.println("u must respond to start request first !!!!!!");
        }
    }

    @Override
    public void analyseComingMessage(String comingMessage) {

        if (comingMessage.matches(Request.PLAYERS_LIST)) {

            currentGame.addPlayer(username);

        } else if (comingMessage.matches(Request.PLAYERS_INFOS)) {

            String message=comingMessage.substring(comingMessage.indexOf("PLAYER"));
            String[] data=message.split("\\s+");

            Player tmp=currentGame.getPlayers().remove(currentGame.getPlayers().size()-1);

            for(int i=1;i< data.length;i++){
                currentGame.addPlayer(data[i]);
            }
            currentGame.getPlayers().add(tmp);


        }else if (comingMessage.matches(Request.PLAYER_JOINED)) {

            String name = comingMessage.substring(4, comingMessage.length() - 7);
            currentGame.addPlayer(name);
            writeToServer("141 " + name + " ACK");

        } else if (comingMessage.matches(Request.START_IS_REQUESTED)) {

            this.startRequested=true;

        } else if (comingMessage.matches(Request.GAME_STARTED)) {

            if(this.currentGame.getType()==1) {
                this.client.setGameState(new PlayingTexasHoldemState(client, username, currentGame));
            }else{
                this.client.setGameState(new Playing5CardPokerState(client, username, currentGame));
            }

        } else if (comingMessage.matches(Request.GAME_ABORDED)) {

            this.startRequested=false;

        }else if (comingMessage.matches(Request.QUIT_ACCEPTED)) {

            quit();

        } else if (comingMessage.matches(Request.PLAYER_QUIT)) {

            String name = comingMessage.substring(4, comingMessage.length()-5);
            Player player=currentGame.getPlayer(name);
            currentGame.removePlayer(player);
            writeToServer(Request.QUIT_RECIEVED);

        }else if(comingMessage.matches(Request.STATE)){

            if(!comingMessage.equals("666 WaitingState")) throw new RuntimeException("states not synchronized between server and client found server:"+comingMessage+" required WaitingState");

        }else if(comingMessage.matches(Request.ALL_PLAYERS)){

            String[] plyers=comingMessage.split("\\s+");
            if((plyers.length-3)!=currentGame.getPlayers().size()) throw new RuntimeException("different players length between server and client found server"+plyers[1]+" required "+currentGame.getPlayers().size());
            for (int i=3;i<plyers.length;i++){
                currentGame.getPlayer(plyers[i]);
            }
        }
    }

    @Override
    public void quit() {
        this.client.setGameState(new MenuState(client,username));
    }

    public boolean getStartRequest(){
        return startRequested;
    }

    public PokerGame getCurrentGame() {
        return currentGame;
    }
}
