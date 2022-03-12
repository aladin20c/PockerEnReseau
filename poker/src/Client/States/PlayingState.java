package Client.States;

import Client.CRoom;
import Client.Player;
import Client.Client;
import Game.Card;
import Game.Utils.Request;

import java.util.ArrayList;

public class PlayingState extends GameState{

    private String username;
    private CRoom currentRoom;

    private String futureAction;
    private String futureChange;

    ArrayList<Card> cards;


    public PlayingState(Client client, String username, CRoom currentRoom) {
        super(client, 3);
        this.username = username;
        this.currentRoom = currentRoom;
        this.futureAction="";
        this.futureChange="";
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
            if(player==null) throw new RuntimeException("player "+username+" is not found");
            player.setHasFolded(true);
            writeToServer(Request.ACTION_RECIEVED);

        } else if (comingMessage.matches(Request.PLAYER_CHECK)) {

            String username = comingMessage.substring(4, comingMessage.length() - 6);
            Player player = currentRoom.getPlayer(username);
            if(player==null) throw new RuntimeException("player "+username+" is not found");
            writeToServer(Request.ACTION_RECIEVED);

        } else if (comingMessage.matches(Request.PLAYER_CALL)) {

            String username = comingMessage.substring(4, comingMessage.length() - 5);
            Player player = currentRoom.getPlayer(username);
            if(player==null) throw new RuntimeException("player "+username+" is not found");

            player.changeStack(-(currentRoom.getHighestBid()-player.getBids()));
            player.setBids(currentRoom.getHighestBid());

            writeToServer(Request.ACTION_RECIEVED);

        }
        else if (comingMessage.matches(Request.PLAYER_RAISE)) {

            int raise=Integer.parseInt(comingMessage.substring(comingMessage.lastIndexOf("RAISE ")));
            String username = comingMessage.substring(4, comingMessage.lastIndexOf(" RAISE"));
            Player player = currentRoom.getPlayer(username);
            if(player==null) throw new RuntimeException("player "+username+" is not found");
            player.changeStack(-(currentRoom.getHighestBid()+raise-player.getBids()));
            currentRoom.incrementHighestBid(raise);
            player.setBids(currentRoom.getHighestBid());
            writeToServer(Request.ACTION_RECIEVED);

        } else if (comingMessage.matches(Request.ACTION_ACCEPTED)) {

            if (futureAction.equals("")) throw new RuntimeException("there is no action sent");
            analyseComingMessage(futureAction);
            futureAction = "";

        }else if(comingMessage.matches(Request.CARDS_DISTRIBUTION)){

            String[] data=comingMessage.substring(10).split("\\s+");
            for(int i=1;i<data.length;i++)this.cards.add(new Card(data[i]));
            writeToServer(Request.CARDS_RECIEVED);

        }else if (comingMessage.matches(Request.CHANGE_ACCEPTED)) {

            if(futureChange.isEmpty()) throw new RuntimeException("there is no change sent");
            String[] data=futureChange.split("\\s+");
            for(int i=1;i<data.length;i++){
                for(int j=0;j< cards.size();j++){
                    if(cards.get(j).encodedTo(data[i])) {
                        cards.remove(j);
                        break;
                    }
                }
            }
            this.futureChange="";

        }else if (comingMessage.matches(Request.PLAYER_CHANGED_CARDS)) {

            String username = comingMessage.substring(4, comingMessage.lastIndexOf(" CHANGE"));
            Player player = currentRoom.getPlayer(username);
            if(player==null) throw new RuntimeException("player is not found");
            int numberOfCardsChanged=Integer.parseInt(comingMessage.substring(comingMessage.lastIndexOf("CHANGE")+7));
            writeToServer(Request.CHANGE_RECIEVED);

        } else if (comingMessage.matches(Request.QUIT_ACCEPTED)) {

            quit();//TODO

        } else if (comingMessage.matches(Request.PLAYER_QUIT)) {

            String name = comingMessage.substring(4, comingMessage.length()-5);
            currentRoom.removePlayer(name);//TODO
            writeToServer(Request.QUIT_RECIEVED);

        }
    }

    @Override
    public void quit() {

    }
}
