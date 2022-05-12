package Server;


import Game.PokerGame;
import Game.utils.Request;
import Server.ServerGameStates.Playing5CardPokerState;
import Server.ServerGameStates.PlayingTexasHoldemState;
import Server.ServerGameStates.WaitingState;

import java.util.*;


public class Room  {

    private PokerGame game;
    private List<ClientHandler> clientHandlers;
    private int turn;
    private boolean endgame;
    private boolean resetIsSet;


    public Room() {
        this.clientHandlers= Collections.synchronizedList(new ArrayList<>());
        this.turn=-1;
        this.endgame=false;
        this.resetIsSet=false;
    }


    public synchronized  void addClient(ClientHandler clientHandler){
        clientHandlers.add(clientHandler);
        game.addPlayer(clientHandler.getClientUsername());
    }

    public synchronized  void removeClient(ClientHandler clientHandler){
        clientHandlers.remove(clientHandler);
    }


    public synchronized  ClientHandler getClientHandler(String username){
        for(ClientHandler ch : clientHandlers){
            if(ch.getClientUsername().equals(username)) return ch;
        }
        return null;
    }



    public synchronized  boolean canAddNewClient(){
        if(clientHandlers.get(0).getGameState() instanceof WaitingState){
            return !((WaitingState)clientHandlers.get(0).getGameState()).startRequested() && clientHandlers.size()<game.getMaxPlayers();
        }else {
            return false;
        }
    }


    public synchronized  void requestStart(boolean start){
        for(ClientHandler ch : clientHandlers){
            if(ch.getGameState() instanceof WaitingState) {
                ((WaitingState) ch.getGameState()).setStartRequested(start);
                ch.getGameState().setStartResponse(0);
            }
        }
    }


    public synchronized void setResetGameTimer(){
        if (endgame && !resetIsSet){
            Timer timer=new Timer(true);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    resetGame();
                    timer.cancel();
                }
            },15000);
            resetIsSet=true;
        }
    }



    public synchronized  void resetGame(){
        clientHandlers.add(clientHandlers.remove(0));
        ArrayList<ClientHandler> reClientHandlers=new ArrayList<>();
        for(ClientHandler ch:clientHandlers){
            if(!game.getPlayer(ch.getClientUsername()).canReplay(game) || ch.getGameState().getEndgameResponse()!=1){
                reClientHandlers.add(ch);
            }
        }
        for (ClientHandler ch:reClientHandlers){
            ch.getGameState().clientQuit();
        }

        if(game.canResetGame()){
            this.endgame=false;
            this.resetIsSet=false;
            this.turn=-1;
            game.resetGame();
            for(ClientHandler ch : clientHandlers){
                ch.getGameState().setEndgameResponse(0);
            }
            clientHandlers.get(0).getGameState().broadCastMessageToEveryone(Request.GAME_STARTED);
            if(game.getType()==1){
               ((PlayingTexasHoldemState) clientHandlers.get(0).getGameState()).rotateTurn();
            }else {
                ((Playing5CardPokerState) clientHandlers.get(0).getGameState()).rotateTurn();
            }
        }
    }


    public synchronized String informationToString(int index){
        return "121 MESS "+index+" ID "+game.getId()+" "+game.getType()+" "+game.getMaxPlayers()+" "+game.getMinBid()+" "+game.getInitStack()+" "+clientHandlers.size();
    }
    public synchronized  boolean isAdmin(ClientHandler clientHandler){
        return clientHandlers.get(0)==clientHandler;
    }
    public synchronized  List<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }
    public synchronized  int numberOfClients(){
        return clientHandlers.size();
    }
    public synchronized  boolean isEmpty(){
        return clientHandlers.isEmpty();
    }
    public synchronized  PokerGame getGame() {
        return game;
    }
    public synchronized  void setGame(PokerGame game) {
        this.game = game;
    }
    public synchronized  int getTurn() {
        return turn;
    }
    public synchronized  boolean isEndgame() {
        return endgame;
    }
    public synchronized  void setEndgame(boolean endgame) {
        this.endgame = endgame;
    }
    public synchronized  boolean turnIsUpToDate(){
        return turn==game.getBidTurn();
    }
    public synchronized  void updateTurn(){
        this.turn=game.getBidTurn();
    }
}
