package Client.States;

import Client.Client;
import Game.PokerFerme;
import Game.PokerGame;
import Game.TexasHoldem;
import Game.utils.Request;

public class MenuState extends GameState{

    private final String username;
    private PokerGame futureGame;
    private PokerGame[] gamesList;
    private int n=0;

    public MenuState(Client client, String username) {
        super(client);
        this.username = username;
    }


    @Override
    public void analyseMessageToSend(String messageToSend) {
        if (messageToSend.matches(Request.CREATE_ROOM)) {
            String[] words = messageToSend.substring(11).split("\\s*[a-zA-Z]+\\s+");

            int type = Integer.parseInt(words[0]);
            int numberOfPlayers = Integer.parseInt(words[1]);
            int minBet = Integer.parseInt(words[2]);
            int initialStack = Integer.parseInt(words[3]);
            //preparing the room that will be created by the user and that he ll join
            if (futureGame == null) futureGame = (type==1)?new TexasHoldem(0,type, numberOfPlayers, minBet, initialStack):new PokerFerme(0,type, numberOfPlayers, minBet, initialStack);
        }
    }
    
    

    @Override
    public void analyseComingMessage(String comingMessage) {
        if(comingMessage.matches(Request.INCORRECT_VALUE)){
            if(client.getClientFrame() != null) {
                client.getClientFrame().setIncorrectType(true);
            }
        }
        else{
            if(comingMessage.matches(Request.INCORRECT_PLAYERS)){
                if(client.getClientFrame() != null) {
                    client.getClientFrame().setIncorrectPlayers(true);
                }
            }
            else{
                if(comingMessage.matches(Request.INCORRECT_BET)){
                    if(client.getClientFrame() != null) {
                    client.getClientFrame().setIncorrectBet(true);
                    }
                }
                else{
                    if(comingMessage.matches(Request.INCORRECT_STACK)){
                        if(client.getClientFrame() != null) {
                            client.getClientFrame().setIncorrectStack(true);
                        }
                    }
                    else{

                        if (comingMessage.matches(Request.GAME_CREATED)) {

                            int gameId = Integer.parseInt(comingMessage.substring(17));
                            if (futureGame==null) throw new RuntimeException("player is not attempting to create a room");
                            futureGame.setId(gameId);
                            futureGame.addPlayer(username);
                            this.client.setGameState(new WaitingState(client,username,futureGame));

                        }else if (comingMessage.matches(Request.LIST_LENGTH)) {

                            int length = Integer.parseInt(comingMessage.substring(11));
                            n=length;
                            if (length != 0) this.gamesList = new PokerGame[length];

                        } else if (comingMessage.matches(Request.ROOM_INFOS)) {

                            int index = Integer.parseInt(comingMessage.substring(9, comingMessage.indexOf(" ID")));
                            String Infos = comingMessage.substring(comingMessage.indexOf("ID") + 2);
                            String[] attributes = Infos.split("\\s+");
                            int id = Integer.parseInt(attributes[1]);
                            int type = Integer.parseInt(attributes[2]);
                            int numberOfPlayers = Integer.parseInt(attributes[3]);
                            int minBet = Integer.parseInt(attributes[4]);
                            int initialStack = Integer.parseInt(attributes[5]);
                            int existingPlayers = Integer.parseInt(attributes[6]);
                            this.gamesList[index - 1] = (type==1)? new TexasHoldem(id,type, numberOfPlayers, minBet, initialStack):new PokerFerme(id,type, numberOfPlayers, minBet, initialStack);

                        } else if (comingMessage.matches(Request.ROOM_JOINED)) {

                            int id = Integer.parseInt(comingMessage.substring(9, comingMessage.length() - 7));
                            if (this.gamesList==null) throw new RuntimeException("player is not attempting to join a room");
                            for (PokerGame game : gamesList) {
                                if (game.getId() == id) {
                                    this.client.setGameState(new WaitingState(client,username,game));
                                    return;
                                }
                            }
                            throw new RuntimeException("room not found");

                        }else if(comingMessage.matches(Request.STATE)){

                            if(!comingMessage.equals("666 MenuState")) throw new RuntimeException("states not synchronized between server and client found "+comingMessage+" required MenuState");

                        }
                    }
                }
            }
        }

    }
    public String getUserName(){
        return username;
    }
    public PokerGame[] getGamesList(){
        return gamesList;
    }
    public PokerGame getGamesList(int i){
        return gamesList[i];
    }
    public int getN(){
        return n;
    }
}
