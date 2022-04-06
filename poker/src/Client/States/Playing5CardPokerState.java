package Client.States;

import Client.Client;
import Game.Card;
import Game.Hand;
import Game.Player;
import Game.PokerGame;
import Game.Utils.Request;


public class Playing5CardPokerState extends GameState{

    int turn;
    private String username;
    private PokerGame currentGame;
    private String futureAction;
    private String futureChange;


    public Playing5CardPokerState(Client client, String username, PokerGame currentGame) {
        super(client, 3);
        System.out.println("[Client][gameState][Playing5CardPokerState] playing 5 card poker....");
        this.username = username;
        this.currentGame = currentGame;
        this.futureAction="";
        this.futureChange="";
        this.turn=-1;
        startGame();
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
            Player player = currentGame.getPlayer(username);
            player.fold(currentGame);
            writeToServer(Request.ACTION_RECIEVED);
            rotateTurn();

        } else if (comingMessage.matches(Request.PLAYER_CHECK)) {

            String username = comingMessage.substring(4, comingMessage.length() - 6);
            Player player = currentGame.getPlayer(username);
            player.check(currentGame);
            writeToServer(Request.ACTION_RECIEVED);
            rotateTurn();

        } else if (comingMessage.matches(Request.PLAYER_CALL)) {

            String username = comingMessage.substring(4, comingMessage.length() - 5);
            Player player = currentGame.getPlayer(username);
            player.call(currentGame);
            writeToServer(Request.ACTION_RECIEVED);
            rotateTurn();

        }
        else if (comingMessage.matches(Request.PLAYER_RAISE)) {

            int raise=Integer.parseInt(comingMessage.substring(comingMessage.lastIndexOf("RAISE ")));
            String username = comingMessage.substring(4, comingMessage.lastIndexOf(" RAISE"));
            Player player = currentGame.getPlayer(username);
            player.raise(currentGame,raise);
            writeToServer(Request.ACTION_RECIEVED);
            rotateTurn();

        } else if (comingMessage.matches(Request.ACTION_ACCEPTED)) {

            if (futureAction.equals("")) throw new RuntimeException("there is no action sent");
            Player player=currentGame.getPlayer(username);

            if(futureAction.equals("FOLD")){
                player.fold(currentGame);
            }else if(futureAction.equals("CHECK")){
                player.check(currentGame);
            }else if(futureAction.equals("CALL")){
                player.call(currentGame);
            }else if(futureAction.startsWith("RAISE")){
                player.raise(currentGame,Integer.parseInt(futureAction.substring(6)));
            }
            futureAction = "";
            rotateTurn();


        }else if(comingMessage.matches(Request.CARDS_DISTRIBUTION)){

            String[] data=comingMessage.substring(10).split("\\s+");
            Hand hand=currentGame.getPlayer(username).getHand();
            for(int i=1;i<data.length;i++) hand.add(new Card(data[i]));
            writeToServer(Request.CARDS_RECIEVED);

        }else if (comingMessage.matches(Request.CHANGE_ACCEPTED)) {

            if(futureChange.isEmpty()) throw new RuntimeException("there is no change sent");
            String[] data=futureChange.split("\\s+");
            Card[] cards=new Card[data.length-1];
            for(int i=1;i<data.length;i++){
                cards[i-1]=new Card(data[i]);
            }
            currentGame.getPlayer(username).getHand().removeAll(cards);
            this.futureChange="";
            rotateTurn();

        }else if (comingMessage.matches(Request.PLAYER_CHANGED_CARDS)) {

            String username = comingMessage.substring(4, comingMessage.lastIndexOf(" CHANGE"));
            Player player = currentGame.getPlayer(username);
            int numberOfCardsChanged=Integer.parseInt(comingMessage.substring(comingMessage.lastIndexOf("CHANGE")+7));
            writeToServer(Request.CHANGE_RECIEVED);
            rotateTurn();

        } else if (comingMessage.matches(Request.QUIT_ACCEPTED)) {

            quit();

        } else if (comingMessage.matches(Request.PLAYER_QUIT)) {

            String username = comingMessage.substring(4, comingMessage.length()-5);
            Player player = currentGame.getPlayer(username);
            player.quit(currentGame);
            writeToServer(Request.QUIT_RECIEVED);
            rotateTurn();
        }
    }

    @Override
    public void quit() {
        this.client.setGameState(new MenuState(client,username));
    }



    public void startGame(){
        currentGame.setCurrentPlayer(currentGame.nextPlayer(0));
        rotateTurn();
    }


    public void rotateTurn(){
        if(currentGame.isRoundFinished()){
            System.out.println("client : endgame");return;
        }

        if(turn!=currentGame.getBidTurn()){
            turn=currentGame.getBidTurn();
            switch (turn) {
                case 0:
                    System.out.println("client : Ante");
                    break;
                case 1:
                    System.out.println("client : second betting round");
                    break;
                case 2:
                    System.out.println("client : changing round");
                    break;
                case 3:
                    System.out.println("client : third betting round");
                    break;
                default: System.out.println("client : endgame");
            }
        }

        if(currentGame.isCurrentPlayer(username)) {
            System.out.println("client : It is ur turn");
        }else{
            System.out.println("client : It is "+currentGame.getCurrentPlayer().getName()+"'s turn");
        }
    }

}
