package Client;


import Game.Card;
import Game.Utils.Request;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    private Player player;
    private CRoom futureRoom;
    private CRoom[] roomsList;
    private CRoom currentRoom;
    private String username;

    private String futureAction;
    private String futureChange;

    ArrayList<Card> cards;





    public Client(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = "";
            this.futureAction="";
            this.futureChange="";
            this.cards=new ArrayList<>();
        }catch (IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }
    public void closeEverything(Socket socket,BufferedReader bufferedReader,BufferedWriter bufferedWriter){
        try{
            //closing the outer wrapper will close the underlying streams (ex:outputStreamReader)
            if(bufferedReader!=null) bufferedReader.close();
            if(bufferedWriter!=null) bufferedWriter.close();
            if(socket!=null) socket.close();//closing sockets will close socket input/output streams
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void writeToServer(String messageToSend){
        try {
            bufferedWriter.write(messageToSend);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        }catch (IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }
    /*send messages to the clientHandler(the connection that the server has spawned to handle a client)*/
    public void sendMessage(){
        Scanner scanner=new Scanner(System.in);
        while (socket.isConnected()){
            //get what the user is typing and sent it over
            String messageToSend=scanner.nextLine(); //when enter is pressed in the terminal, wht he typed will be captured here
            messageToSend=messageToSend.trim();
            analyseMessageToSend(messageToSend);
            writeToServer(messageToSend);
        }
    }
    /*making a seperate thread for listening for messages that has been broadCasted*/
    public void listenForMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String comingMessage;

                while (socket.isConnected()){
                    try {
                        comingMessage = bufferedReader.readLine();
                        analyseComingMessage(comingMessage);
                        System.out.println(comingMessage);
                    }catch(NullPointerException e){
                        System.out.println("server has shut down");
                        closeEverything(socket,bufferedReader,bufferedWriter);
                    }catch (IOException e){
                        closeEverything(socket,bufferedReader,bufferedWriter);
                    }
                }
            }
        }).start();
    }


    /*analysing messages before sending it to the server */
    public void analyseMessageToSend(String messageToSend){
        if(joinedARoom() && currentRoom.startRequested() && !messageToSend.matches(Request.START_RESPONSE)){
            System.out.println("u must respond to the start request first !!!!!");
        } else if(messageToSend.matches(Request.CREATE_ROOM)){
            String[] words=messageToSend.substring(11).split("\\s*[a-zA-Z]+\\s+");

            int type=Integer.parseInt(words[0]);
            int numberOfPlayers=Integer.parseInt(words[1]);
            int minBet=Integer.parseInt(words[2]);
            int initialStack=Integer.parseInt(words[3]);

            //preparing the room that will be created by the user and that he ll join
            if(futureRoom==null) futureRoom=new CRoom(type,numberOfPlayers,minBet,initialStack);

        }else if(messageToSend.matches(Request.FOLD)){

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



    public void analyseComingMessage(String comingMessage) {

        if (comingMessage.matches(Request.GAME_CREATED)) {

            int gameId = Integer.parseInt(comingMessage.substring(17));
            if (futureRoom==null) throw new RuntimeException("player is not attempting to create a room");
            currentRoom = futureRoom;
            currentRoom.setId(gameId);
            joinRoom(currentRoom);
            currentRoom.addPlayer(this.username);

        } else if (comingMessage.matches(Request.LIST_LENGTH)) {

            int length = Integer.parseInt(comingMessage.substring(11));
            if (length != 0) this.roomsList = new CRoom[length];

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
            this.roomsList[index - 1] = new CRoom(id, type, numberOfPlayers, minBet, initialStack);

        } else if (comingMessage.matches(Request.ROOM_JOINED)) {

            int id = Integer.parseInt(comingMessage.substring(9, comingMessage.length() - 7));
            if (this.roomsList==null) throw new RuntimeException("player is not attempting to join a room");
            for (CRoom r : roomsList) {
                if (r.getId() == id) joinRoom(r);
            }
            if (!joinedARoom()) throw new RuntimeException("room not found");

        } else if (comingMessage.matches(Request.EXISTING_PLAYER)) {

            if (!this.joinedARoom()) throw new RuntimeException("player is not in a room");
            String[] players=comingMessage.split("\\s+");
            for(int i=1;i<players.length;i++) currentRoom.addPlayer(players[i]);

        } else if (comingMessage.matches(Request.PLAYER_JOINED)) {

            String name = comingMessage.substring(4, comingMessage.length() - 7);
            currentRoom.addPlayer(name);
            writeToServer("141 " + name + " ACK");

        } else if (comingMessage.matches(Request.START_IS_REQUESTED)) {

            currentRoom.setStartRequested(true);

        } else if (comingMessage.matches(Request.GAME_STARTED)) {

            currentRoom.setStartRequested(false);
            currentRoom.setGameStarted(true);

        } else if (comingMessage.matches(Request.GAME_ABORDED)) {

            currentRoom.setStartRequested(false);
            currentRoom.setGameStarted(false);


        } else if (comingMessage.matches(Request.PLAYER_FOLD)) {

            String username = comingMessage.substring(4, comingMessage.length() - 5);
            Player player = currentRoom.getPlayer(username);
            if(player==null) throw new RuntimeException("player "+username+" is not found");
            player.hasFolded=true;
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
            player.stack=player.stack-(currentRoom.getHighestBid()-player.bids);
            player.bids=currentRoom.getHighestBid();
            writeToServer(Request.ACTION_RECIEVED);

        }
        else if (comingMessage.matches(Request.PLAYER_RAISE)) {

            int raise=Integer.parseInt(comingMessage.substring(comingMessage.lastIndexOf("RAISE ")));
            String username = comingMessage.substring(4, comingMessage.lastIndexOf(" RAISE"));
            Player player = currentRoom.getPlayer(username);
            if(player==null) throw new RuntimeException("player "+username+" is not found");
            player.stack=player.stack-(currentRoom.getHighestBid()+raise-player.bids);
            currentRoom.incrementHighestBid(raise);
            player.bids=currentRoom.getHighestBid();
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
            for(int i=0;i<Integer.parseInt(data[0]);i++) {
                //TODO give the player new cards; not given in the project sheet
            }
            this.futureChange="";

        }else if (comingMessage.matches(Request.PLAYER_CHANGED_CARDS)) {

            String username = comingMessage.substring(4, comingMessage.lastIndexOf(" CHANGE"));
            Player player = currentRoom.getPlayer(username);
            if(player==null) throw new RuntimeException("player is not found");
            int numberOfCardsChanged=Integer.parseInt(comingMessage.substring(comingMessage.lastIndexOf("CHANGE")+7));
            writeToServer(Request.CHANGE_RECIEVED);

        } else if (comingMessage.matches(Request.QUIT_ACCEPTED)) {

            quitRoom();

        } else if (comingMessage.matches(Request.PLAYER_QUIT)) {

            String name = comingMessage.substring(4, comingMessage.length()-5);
            currentRoom.playerQuit(name);
            writeToServer(Request.QUIT_RECIEVED);

        }
    }




    public boolean joinedARoom(){
        return currentRoom!=null;
    }
    public void joinRoom(CRoom room){
        currentRoom=room;
        this.player=new Player(username,currentRoom.getInitStack());
        futureRoom=null;
        roomsList=null;
    }
    public void quitRoom(){
        currentRoom.playerQuit(this.username);
        this.player = null;
        this.futureRoom = null;
        this.roomsList = null;
        this.currentRoom = null;
        futureAction="";
        futureChange="";
        cards.clear();
    }



    public static void main(String[] args) {
        try {

            Socket socket = new Socket("localhost", Request.PORT);
            Client client = new Client(socket);

            //setting the name
            Scanner scanner = new Scanner(System.in);
            while (client.username.isEmpty()){
                System.out.println("please enter your name");
                String username = scanner.nextLine().trim();
                client.writeToServer("100 HELLO PLAYER "+username);
                String comingMessage=client.bufferedReader.readLine();
                System.out.println("SERVER :"+comingMessage);
                if(comingMessage.equals("101 WELCOME "+username)) client.username=username;
            }

            //after the client has connected, he can now listen and write messages to server
            client.listenForMessage();
            client.sendMessage();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
