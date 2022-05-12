package Server;


import Game.PokerGame;
import Game.utils.Request;
import Server.ServerGameStates.Playing5CardPokerState;
import Server.ServerGameStates.PlayingTexasHoldemState;
import Server.ServerGameStates.WaitingState;

import java.io.IOException;
import java.util.*;


public class Room  {

    private PokerGame game;
    private List<ClientHandler> clientHandlers;
    private int turn;
    private boolean startRequested;
    private boolean endgame;
    private boolean resetIsSet;


    public Room() {
        this.clientHandlers= Collections.synchronizedList(new ArrayList<>());
        this.turn=-1;
        this.startRequested=false;
        this.endgame=false;
        this.resetIsSet=false;
    }

    public synchronized void broadCastMessage(String messageToSend,ClientHandler ch){
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (clientHandler!=ch) {
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
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                clientHandler.getBufferedWriter().write(messageToSend);
                clientHandler.getBufferedWriter().newLine();
                clientHandler.getBufferedWriter().flush();
            } catch (IOException e) {
                clientHandler.closeEverything();
            }
        }
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
            return !startRequested && clientHandlers.size()<game.getMaxPlayers();
        }else {
            return false;
        }
    }


    public synchronized void setResetGameTimer(){
        if (endgame && !resetIsSet && game.canResetGame()){
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

    public synchronized void purgeTasks(){
        for (ClientHandler ch : clientHandlers){
            ch.purge();
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




    public synchronized boolean isStartRequested() {
        return startRequested;
    }

    public synchronized void setStartRequested(boolean startRequested) {
        this.startRequested = startRequested;
        for (ClientHandler ch :clientHandlers){
            ch.getGameState().setStartResponse(0);
        }
    }
}
