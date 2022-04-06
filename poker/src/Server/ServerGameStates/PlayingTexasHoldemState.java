package Server.ServerGameStates;

import Game.Card;
import Game.Player;
import Game.TexasHoldem;
import Game.Utils.Request;
import Server.ClientHandler;
import Server.Room;
import Server.Server;

public class PlayingTexasHoldemState extends GameState{


    private Player player;


    public PlayingTexasHoldemState(ClientHandler clientHandler , Room room) {
        super(clientHandler, room);
        this.player=room.getGame().getPlayer(clientHandler.getClientUsername());
        startGame();
    }


    @Override
    public void analyseRequest(String messageFromClient) {
        if(!messageFromClient.matches("\\d\\d\\d.+")) {

            broadCastMessage(clientHandler.getClientUsername()+":"+messageFromClient);

        }else if(messageFromClient.matches(Request.FOLD)){

            if(!room.getGame().canFold(player)){
                writeToClient("907 Invalid Command");
            }else {
                player.fold(room.getGame());
                broadCastMessage("510 " + clientHandler.getClientUsername() + " FOLD");
                writeToClient("400 ACCEPTED");
                rotateTurn();
            }

        }else if(messageFromClient.matches(Request.CHECK)){

            if(!room.getGame().canCheck(player)){
                writeToClient("907 Invalid Command");
            }else{
                player.check(room.getGame());
                writeToClient("400 ACCEPTED");
                broadCastMessage("511 "+ clientHandler.getClientUsername() +" CHECK");
                rotateTurn();
            }

        }else if(messageFromClient.matches(Request.CALL)){

            if(!room.getGame().canCall(player)){
                writeToClient("907 Invalid Command");
            }else{
                player.call(room.getGame());
                writeToClient("400 ACCEPTED");
                broadCastMessage("512 "+ clientHandler.getClientUsername() +" CALL");
                rotateTurn();
            }

        }else if(messageFromClient.matches(Request.RAISE)) {

            int raise = Integer.parseInt(messageFromClient.substring(10));
            if(!room.getGame().canRaise(player,raise)){
                writeToClient("907 Invalid Command");
            }else {
                player.raise(room.getGame(), raise);
                writeToClient("400 ACCEPTED");
                broadCastMessage("513 " + clientHandler.getClientUsername() + " RAISE " + raise);
                rotateTurn();
            }

        }else if(messageFromClient.matches(Request.ACTION_RECIEVED)){

            //Nothing here (probably)

        }else if(messageFromClient.matches(Request.CARDS_RECIEVED)){

            //Nothing here (probably)

        }else if (messageFromClient.matches(Request.QUIT)) {

            clientQuit();

        } else if (messageFromClient.matches(Request.QUIT_RECIEVED)) {

            //nothing to do here(probably)

        }else{
            clientHandler.writeToClient(Request.ERROR);
        }
    }



    @Override
    public void clientQuit() {
        writeToClient(Request.QUIT_ACCEPTED);
        broadCastMessage("211 " + clientHandler.getClientUsername() + " QUIT");
        player.quit(room.getGame());
        room.removeClient(clientHandler);
        if(room.numberOfClients()==0){
            Server.removeRoom(room);
        }
        clientHandler.setGameState(new MenuState(clientHandler));
        //TODO further actions
    }


    public void startGame(){
        if(room.isAdmin(clientHandler)){
            room.getGame().setCurrentPlayer(room.getGame().nextPlayer(0));
            rotateTurn();
        }
    }

    public void rotateTurn(){
        if(room.getGame().isRoundFinished()){
            broadCastMessageToEveryone("server : EndGame");
            //todo
        }else if(!room.turnIsUpToDate()){
            room.updateTurn();
            switch (room.getTurn()){
                case 0:
                    broadCastMessageToEveryone("server : preflop");
                    ((TexasHoldem)room.getGame()).fixSmallBigBlind();
                    broadCastMessageToEveryone("server : small blind and bigblind paid");
                    room.getGame().burn();
                    room.getGame().distributeCards(2);
                    notifyCardDistribution();
                    break;
                case 1:
                    broadCastMessageToEveryone("server : flop");
                    room.getGame().burn();
                    revealCards(3);
                    break;

                case 2:
                    broadCastMessageToEveryone("server : turn");
                    room.getGame().burn();
                    revealCards(1);
                    break;
                case 3:
                    broadCastMessageToEveryone("server : river");
                    room.getGame().burn();
                    revealCards(1);
                    break;
                default: broadCastMessageToEveryone("server : endgame");
            }
        }
        broadCastMessageToEveryone("Server : It is "+room.getGame().getCurrentPlayer().getName()+"'s turn");
    }


    public void notifyCardDistribution(){
        for(Player player : room.getGame().getPlayers()){
            Card[] cards= player.getCards();
            String cardDistribution="610 CARDS ";
            cardDistribution+=cards.length;
            for(Card card : cards) cardDistribution+=(" "+card.toString());
            room.getClientHandler(player.getName()).writeToClient(cardDistribution);
        }
    }

    public void revealCards(int n){
        Card[] cards= room.getGame().revealCards(n);
        String cardDistribution="610 CARDS "+n;
        for(Card card : cards) cardDistribution+=(" "+card.toString());
        broadCastMessageToEveryone(cardDistribution);
    }

}

