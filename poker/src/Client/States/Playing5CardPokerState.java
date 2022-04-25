package Client.States;

import Client.Client;
import Game.Card;
import Game.Hand;
import Game.Player;
import Game.PokerGame;
import Game.utils.Request;



public class Playing5CardPokerState extends GameState{

    private int turn;
    private String username;
    private PokerGame currentGame;
    private String futureAction;
    private String futureChange;
    private boolean endgame;


    public Playing5CardPokerState(Client client, String username, PokerGame currentGame) {
        super(client);
        this.username = username;
        this.currentGame = currentGame;
        this.futureAction = "";
        this.futureChange="";
        this.turn=-1;
        this.endgame=false;
        startGame();
    }

    public void startGame(){
        currentGame.setCurrentPlayer(currentGame.nextPlayer(0));
        rotateTurn();
    }


    @Override
    public void analyseMessageToSend(String messageToSend) {
        if (messageToSend.matches(Request.FOLD)) {
            futureAction ="FOLD";
        } else if (messageToSend.matches(Request.CHECK)) {
            futureAction = "CHECK";
        } else if (messageToSend.matches(Request.CALL)) {
            futureAction = "CALL";
        } else if (messageToSend.matches(Request.RAISE)) {
            int raise = Integer.parseInt(messageToSend.substring(10));
            futureAction = "RAISE " + raise;
        }


        else if(messageToSend.matches(Request.CHANGE)){
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

            int raise=Integer.parseInt(comingMessage.substring(comingMessage.lastIndexOf("RAISE ")+6));
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


        } else if (comingMessage.matches(Request.CARDS_DISTRIBUTION)) {

            String[] data = comingMessage.substring(10).split("\\s+");
            Hand hand = currentGame.getPlayer(username).getHand();

            if (hand.getCards().size() < 2) {
                for (int i = 1; i < data.length; i++) hand.add(new Card(data[i]));
            } else {
                for (int i = 1; i < data.length; i++) currentGame.getTable().add(new Card(data[i]));
            }
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

        }else if (comingMessage.matches(Request.QUIT_ACCEPTED)) {

            quit();

        } else if (comingMessage.matches(Request.PLAYER_QUIT)) {

            String name = comingMessage.substring(4, comingMessage.length()-5);
            Player player=currentGame.getPlayer(name);
            player.quit(currentGame);
            writeToServer(Request.QUIT_RECIEVED);
            rotateTurn();

        }else if(comingMessage.matches(Request.WINNERS)){

            /**String[] data=comingMessage.split("\\s+");
             for (int i=1;i<data.length-1;i++){
             currentGame.getWinners().add(currentGame.getPlayer(data[i]));
             }
             endgame=true;
             writeToServer(Request.WINRECEIVED);

             Timer timer=new Timer(true);
             timer.schedule(new TimerTask() {
            @Override
            public void run() {
            currentGame.resetGame();
            rotateTurn();
            }
            },15_000);*/


        }else if(comingMessage.matches(Request.WINNERSANDCARDS)){

            String[] data=comingMessage.split("\\s+");
            Player player=currentGame.getPlayer(data[1]);
            for (int i=4;i<data.length;i++){
                player.getHand().add(new Card(data[i]));
            }

        }else if(comingMessage.matches(Request.STATE)){

            if(!comingMessage.equals("666 PlayingTexasHoldemState")) throw new RuntimeException("states not synchronized between server and client found "+comingMessage+" required PlayingTexasHoldemState");

        }else if(comingMessage.matches(Request.ALL_PLAYERS)){

            String[] players=comingMessage.split("\\s+");
            if((players.length-3)!=currentGame.getPlayers().size()) throw new RuntimeException("different players length between server and client found server"+players[1]+" required "+currentGame.getPlayers().size());
            for (int i=3;i<players.length;i++){
                currentGame.getPlayer(players[i]);
            }
        }else if(comingMessage.matches(Request.ACTIVE_PLAYERS)){

            String[] players=comingMessage.split("\\s+");
            int count=0;
            for (int i=3;i<players.length;i++){
                Player player=currentGame.getPlayer(players[i]);
                if(!player.hasQuitted()) {
                    count++;
                }else {
                    throw new RuntimeException(players[i]+" supposed to be active");
                }
            }
            if((players.length-3)!=count) throw new RuntimeException("different players length between server and client found server"+players[1]+" required "+currentGame.getPlayers().size());

        }else if(comingMessage.matches(Request.QUITTED_PLAYERS)){

            String[] players=comingMessage.split("\\s+");
            int count=0;
            for (int i=3;i<players.length;i++){
                Player player=currentGame.getPlayer(players[i]);
                if(player.hasQuitted()) {
                    count++;
                }else {
                    throw new RuntimeException(players[i]+" supposed to have quit");
                }
            }
            if((players.length-3)!=count) throw new RuntimeException("different players length between server and client found server"+players[1]+" required "+currentGame.getPlayers().size());

        }else if(comingMessage.matches(Request.FOLDED_PLAYERS)){

            String[] players=comingMessage.split("\\s+");
            int count=0;
            for (int i=3;i<players.length;i++){
                Player player=currentGame.getPlayer(players[i]);
                if(player.hasFolded()) {
                    count++;
                }else {
                    throw new RuntimeException(players[i]+" supposed to have folded");
                }
            }
            if((players.length-3)!=count) throw new RuntimeException("different players length between server and client found server"+players[1]+" required "+currentGame.getPlayers().size());
        }
    }

    @Override
    public void quit() {
        this.client.setGameState(new MenuState(client, username));
    }



    public void rotateTurn(){
        if(endgame){
            return;
        }else if(currentGame.isRoundFinished()){
            System.out.println("client : endgame");
            endgame=true;
            return;
        }else if(turn!=currentGame.getBidTurn()){
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
                default:
                    endgame=true;
                    System.out.println("client : endgame");
            }
        }
        if(currentGame.isCurrentPlayer(username)) {
            System.out.println("client : It is ur turn");
        }else{
            System.out.println("client : It is "+currentGame.getCurrentPlayer().getName()+"'s turn");
        }
    }
}