package Client.States;

import Client.Client;
import Client.Player;
import Game.Card;
import Game.Hand;
import Game.Room;
import Game.Utils.Request;


public class Playing5CardPokerState extends GameState{

    private String username;
    private Room currentRoom;

    private String futureAction;
    private String futureChange;

    private Hand hand;//same as in player class corresponding to client


    public Playing5CardPokerState(Client client, String username, Room currentRoom) {
        super(client, 3);
        System.out.println("[Client][gameState][Playing5CardPokerState] playing 5 card poker....");
        this.username = username;
        this.currentRoom = currentRoom;
        this.futureAction="";
        this.futureChange="";
        this.hand=this.currentRoom.getPlayer(username).getHand();
        this.startGame();
    }


    @Override
    public void analyseMessageToSend(String messageToSend) {
        if(messageToSend.matches(Request.FOLD)){
            futureAction="510 "+username+" FOLD";
        }else if(messageToSend.matches(Request.CHECK)){
            futureAction="511 "+username+" CHECK";
        }else if(messageToSend.matches(Request.CALL)){
            futureAction="512 "+username+" CALL";
        }else if(messageToSend.matches(Request.RAISE)){
            int raise = Integer.parseInt(messageToSend.substring(10));
            futureAction="513 "+username+" RAISE "+raise;
        }else if(messageToSend.matches(Request.CHANGE)){
            futureChange=messageToSend.substring(11);
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

            int raise=Integer.parseInt(comingMessage.substring(comingMessage.lastIndexOf("RAISE ")));
            String username = comingMessage.substring(4, comingMessage.lastIndexOf(" RAISE"));
            Player player = currentRoom.getPlayer(username);
            player.raise(currentRoom,raise);
            rotateTurn(player);
            writeToServer(Request.ACTION_RECIEVED);

        } else if (comingMessage.matches(Request.ACTION_ACCEPTED)) {

            if (futureAction.equals("")) throw new RuntimeException("there is no action sent");
            analyseComingMessage(futureAction);
            futureAction = "";

        }else if(comingMessage.matches(Request.CARDS_DISTRIBUTION)){

            String[] data=comingMessage.substring(10).split("\\s+");
            for(int i=1;i<data.length;i++)this.hand.add(new Card(data[i]));
            writeToServer(Request.CARDS_RECIEVED);

        }else if (comingMessage.matches(Request.CHANGE_ACCEPTED)) {

            if(futureChange.isEmpty()) throw new RuntimeException("there is no change sent");
            String[] data=futureChange.split("\\s+");
            for(int i=1;i<data.length;i++){
                for(int j=0;j< hand.getCards().size();j++){
                    if(hand.getCards().get(j).encodedTo(data[i])) {
                        hand.getCards().remove(j);
                        break;
                    }
                }
            }
            this.futureChange="";
            rotateTurn(currentRoom.getPlayer(username));

        }else if (comingMessage.matches(Request.PLAYER_CHANGED_CARDS)) {

            String username = comingMessage.substring(4, comingMessage.lastIndexOf(" CHANGE"));
            Player player = currentRoom.getPlayer(username);
            int numberOfCardsChanged=Integer.parseInt(comingMessage.substring(comingMessage.lastIndexOf("CHANGE")+7));
            rotateTurn(player);
            writeToServer(Request.CHANGE_RECIEVED);

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
        this.client.setGameState(new MenuState(client,username));
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
            case 0:break;//ante
            case 1:break;// distributing cards
            case 2:break;//first betting round
            case 3:break;// changing cards
            case 4:break;// secound betting
            case 5:break;// End revealing cards
        }
    }

}
