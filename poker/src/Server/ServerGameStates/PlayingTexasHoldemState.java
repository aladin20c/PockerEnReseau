package Server.ServerGameStates;

import Client.Player;
import Game.Utils.Request;
import Server.ClientHandler;
import Server.SRoom;
import Server.Server;

public class PlayingTexasHoldemState extends GameState{

    private String clientUsername;
    private SRoom currentRoom;
    private Player player;


    public PlayingTexasHoldemState(ClientHandler clientHandler, String clientUsername, SRoom currentRoom) {
        super(clientHandler, 3);
        this.clientUsername=clientUsername;
        this.currentRoom=currentRoom;
        this.player=currentRoom.getPlayer(clientUsername);
        this.player.getHand();
        startGame();
    }


    @Override
    public void analyseRequest(String messageFromClient) {
        if(!messageFromClient.matches("\\d\\d\\d.+")) {

            broadCastMessageToEveryone(clientUsername+":"+messageFromClient,currentRoom.getClientHandlers());

        }else if(messageFromClient.matches(Request.FOLD)){

            if(!currentRoom.isCurrentPlayer(player)){
                writeToClient("907 Invalid Command : this is not ur turn");
            } else if(!player.canFold()){
                writeToClient("907 Invalid Command");
            }else {
                player.fold();
                broadCastMessage("510 " + clientUsername + " FOLD",currentRoom.getClientHandlers());
                writeToClient("400 ACCEPTED");
                rotateTurn();
            }

        }else if(messageFromClient.matches(Request.CHECK)){

            if(!currentRoom.isCurrentPlayer(player)){
                writeToClient("907 Invalid Command : this is not ur turn");
            } else if(!player.canCheck(currentRoom)){
                writeToClient("907 Invalid Command");
            }else{
                player.check();
                writeToClient("400 ACCEPTED");
                broadCastMessage("511 "+ clientUsername +" CHECK",currentRoom.getClientHandlers());
                rotateTurn();
            }

        }else if(messageFromClient.matches(Request.CALL)){

            if(!currentRoom.isCurrentPlayer(player)){
                writeToClient("907 Invalid Command : this is not ur turn");
            } else if(!player.canCall(currentRoom)){
                writeToClient("907 Invalid Command");
            }else{
                player.call(currentRoom);
                writeToClient("400 ACCEPTED");
                broadCastMessage("512 "+ clientUsername +" CALL",currentRoom.getClientHandlers());
                rotateTurn();
            }

        }else if(messageFromClient.matches(Request.RAISE)) {

            int raise = Integer.parseInt(messageFromClient.substring(10));
            if(!currentRoom.isCurrentPlayer(player)){
                writeToClient("907 Invalid Command : this is not ur turn");
            } else if(!player.canRaise(currentRoom,raise)){
                writeToClient("907 Invalid Command");
            }
            player.raise(currentRoom,raise);
            writeToClient("400 ACCEPTED");
            broadCastMessage("513 " + clientUsername + " RAISE " + raise,currentRoom.getClientHandlers());
            rotateTurn();

        }else if(messageFromClient.matches(Request.ACTION_RECIEVED)){

            //Nothing here (probably)

        }else if(messageFromClient.matches(Request.CARDS_RECIEVED)){

            //Nothing here (probably)

        }else if (messageFromClient.matches(Request.QUIT)) {

            clientQuit();

        } else if (messageFromClient.matches(Request.QUIT_RECIEVED)) {

            //nothing to do here(probably)

        }else{
            sendError();
        }
    }



    public void startGame(){
        currentRoom.startGame();
        writeToClient("Srever:distributing cards");
        if(player.dealer){
            //burm first card
            //distribut e cards
            //distribute table cards
        }
        currentRoom.round+=1;
        writeToClient("Srever:first betting Round");

    }



    public void resetGame(){
        currentRoom.resetGame();
    }


    public void rotateTurn(){
        currentRoom.currentPlayer.setPlayedInThisTurn(true);
        currentRoom.setCurrentPlayer(currentRoom.nextPlayer());

        for (Player player : currentRoom.players){
            if(!player.hasFolded && (!player.playedInThisTurn || player.bids!=currentRoom.highestBid)) return;
        }

        //passing to next round
        currentRoom.round+=1;
        currentRoom.setAllPlayersDidntPlay();
        currentRoom. setCurrentPlayer(currentRoom.playerleftToDealer());

        switch (currentRoom.round){
            /*case 0:break;//this is the first betting round (already done in start game)
            case 1:writeToClient("Srever:first betting Round");break;*/
            case 2:broadCastMessageToEveryone("Srever:second betting Round",currentRoom.getClientHandlers());break;
            case 3:broadCastMessageToEveryone("Srever:third betting Round",currentRoom.getClientHandlers());break;
            case 4:broadCastMessageToEveryone("Srever:final Round",currentRoom.getClientHandlers());break;
        }
    }

    


    @Override
    public void clientQuit() {
        writeToClient(Request.QUIT_ACCEPTED);
        broadCastMessage("211 " + clientUsername + " QUIT",currentRoom.getClientHandlers());
        player.setInactive();
        if(currentRoom.isCurrentPlayer(clientUsername)){
            rotateTurn();
        }
        currentRoom.removeClient(clientHandler);
        if(currentRoom.numberOfClients()==0){
            Server.removeRoom(currentRoom);
        }
        //TODO further actions
    }
}
