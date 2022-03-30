package Client.States;

import Client.Client;
import Client.Player;
import Game.Hand;
import Game.Room;
import Game.Utils.Request;


public class PlayingTexasHoldemState extends GameState {

    private String username;
    private Room currentRoom;
    private String futureAction;

    private Hand burntCards;
    private Hand hand;//same as in player class corresponding to client
    private Hand table;


    public PlayingTexasHoldemState(Client client, String username, Room currentRoom) {
        super(client, 3);
        System.out.println("[Client][gameState][PlayingTexasHoldemState] playing Texas Holdem poker....");
        this.username = username;
        this.currentRoom = currentRoom;
        this.futureAction = "";
        this.hand=this.currentRoom.getPlayer(username).getHand();
        this.table=currentRoom.getTable();
        this.burntCards=new Hand();
        this.startGame();
    }


    @Override
    public void analyseMessageToSend(String messageToSend) {
        if (messageToSend.matches(Request.FOLD)) {
            futureAction = "510 " + username + " FOLD";
        } else if (messageToSend.matches(Request.CHECK)) {
            futureAction = "511 " + username + " CHECK";
        } else if (messageToSend.matches(Request.CALL)) {
            futureAction = "512 " + username + " CALL";
        } else if (messageToSend.matches(Request.RAISE)) {
            int raise = Integer.parseInt(messageToSend.substring(10));
            futureAction = "513 " + username + " RAISE " + raise;
        }
    }

    @Override
    public void analyseComingMessage(String comingMessage) {
        if (comingMessage.matches(Request.PLAYER_FOLD)) {

            String username = comingMessage.substring(4, comingMessage.length() - 5);
            Player player = currentRoom.getPlayer(username);
            player.fold();
            rotateTurn(player);
            writeToServer(Request.ACTION_RECIEVED);

        } else if (comingMessage.matches(Request.PLAYER_CHECK)) {

            String username = comingMessage.substring(4, comingMessage.length() - 6);
            Player player = currentRoom.getPlayer(username);
            player.check();
            rotateTurn(player);
            writeToServer(Request.ACTION_RECIEVED);

        } else if (comingMessage.matches(Request.PLAYER_CALL)) {

            String username = comingMessage.substring(4, comingMessage.length() - 5);
            Player player = currentRoom.getPlayer(username);
            player.call(currentRoom);
            rotateTurn(player);
            writeToServer(Request.ACTION_RECIEVED);

        }
        else if (comingMessage.matches(Request.PLAYER_RAISE)) {

            int raise=Integer.parseInt(comingMessage.substring(comingMessage.lastIndexOf("RAISE ")+6));
            String username = comingMessage.substring(4, comingMessage.lastIndexOf(" RAISE"));
            Player player = currentRoom.getPlayer(username);
            player.raise(currentRoom,raise);
            rotateTurn(player);
            writeToServer(Request.ACTION_RECIEVED);

        } else if (comingMessage.matches(Request.ACTION_ACCEPTED)) {

            if (futureAction.equals("")) throw new RuntimeException("there is no action sent");
            analyseComingMessage(futureAction);
            futureAction = "";

        } else if (comingMessage.matches(Request.CARDS_DISTRIBUTION)) {

            /*String[] data = comingMessage.substring(10).split("\\s+");
            if(this.burntCards.isEmpty()){
                for (int i = 1; i < data.length; i++) this.burntCards.add(new Card(data[i]));
            }else if(this.hand.isEmpty() || this.hand.size()<2){
                for (int i = 1; i < data.length; i++) this.hand.add(new Card(data[i]));
            } else {
                for (int i = 1; i < data.length; i++) this.table.add(new Card(data[i]));
            }
            writeToServer(Request.CARDS_RECIEVED);*/

        } else if (comingMessage.matches(Request.QUIT_ACCEPTED)) {

            quit();

        } else if (comingMessage.matches(Request.PLAYER_QUIT)) {

            String username = comingMessage.substring(4, comingMessage.length()-5);
            Player player = currentRoom.getPlayer(username);
            player.setInactive();
            if(currentRoom.isCurrentPlayer(username)){
                rotateTurn(player);
            }
            writeToServer(Request.QUIT_RECIEVED);

        }
    }

    @Override
    public void quit() {
        this.client.setGameState(new MenuState(client, username));
    }

    public void startGame(){
        currentRoom.startGame();
    }
    public void resetGame(){
        currentRoom.resetGame();
    }

    public void rotateTurn(Player player){
        if(currentRoom.currentPlayer!=player)throw new RuntimeException("there s no coordinance between serever and client");
        player.setPlayedInThisTurn(true);
        currentRoom.setCurrentPlayer(currentRoom.nextPlayer());

        for (Player p : currentRoom.players){
            if(!p.hasFolded && (!p.playedInThisTurn || p.bids!=currentRoom.highestBid)) return;
        }
        //passing to next round
        currentRoom.round+=1;
        currentRoom.setAllPlayersDidntPlay();
        currentRoom.setCurrentPlayer(currentRoom.playerleftToDealer());
        switch (currentRoom.round){
            case 0:break;//cards distributed -> already done
            case 1:break;//first betting round
            case 2:break;//second betting round
            case 3:break;//third betting round
            case 4:break;//End revealing cards
        }
    }
}