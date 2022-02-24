package Server;

import Game.Card;
import Game.Utils.Request;


import java.io.*;
import java.net.Socket;
import java.util.ArrayList;


public class ClientHandler implements Runnable{
    

    //used to establish a connection between the client and server
    private Socket socket;

    //used to read data (specifically messages ) that have been sent from the client
    private BufferedReader bufferedReader;

    //used to sent data (specifically messages ) to the client
    private BufferedWriter bufferedWriter;

    private SRoom currentRoom;
    private String clientUsername;
    private int stack;

    private int currentPlayerBids;
    private boolean hasFolded;
    private boolean dealer;
    private ArrayList<Card> cards;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername="";

            this.hasFolded = false;
            this.currentPlayerBids=0;
            this.dealer=false;
        }catch(IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }


    public void broadCastMessage(String messageToSend){
        if(playerIsInARoom()) {
            for (ClientHandler clientHandler : currentRoom.clientHandlers) {
                try {
                    if (!clientHandler.clientUsername.equals(clientUsername)) {
                        clientHandler.bufferedWriter.write(messageToSend);
                        clientHandler.bufferedWriter.newLine();
                        clientHandler.bufferedWriter.flush();
                    }
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        }
    }
    public void writeToClient(String message){
        try{
            this.bufferedWriter.write(message);
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();
        }catch (IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }
    public void broadCastMessageToEveryone(String messageToSend){
        if(playerIsInARoom()) {
            for (ClientHandler clientHandler : currentRoom.clientHandlers) {
                try {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        }
    }


    public void closeEverything(Socket socket,BufferedReader bufferedReader,BufferedWriter bufferedWriter){
        removeClientHandler();
        try{
            //closing the outer wrapper will close the underlying streams (ex:outputStreamReader)
            if(bufferedReader!=null) bufferedReader.close();
            if(bufferedWriter!=null) bufferedWriter.close();
            if(socket!=null) socket.close();//closing sockets will close socket input/output streams
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void removeClientHandler(){
        Server.clientHandlers.remove(this);
        broadCastMessage("211 "+currentRoom+" QUIT");
        if(playerIsInARoom()) currentRoom.clientHandlers.remove(this);
        System.out.println("Server: "+clientUsername+" has left !");
    }

    @Override
    public void run(){
        /*everything here is run on a seperate thread
        we want here to listen for message which is a blocking operation: the program will be stuck until the operaton is completed*/

        String messageFromClient;
        while(socket.isConnected()){
            try{
                messageFromClient=bufferedReader.readLine();
                analyseRequest(messageFromClient);
            }catch(Exception e){
                closeEverything(socket,bufferedReader,bufferedWriter);
                break;//when the client disconnects, we get out of the while loop
            }
        }
    }




    public void analyseRequest(String messageFromClient){
        //if a command begins with three numbers, it is considered as a command, else it is considered as a chat function
        if(!messageFromClient.matches("\\d\\d\\d.*")) {
            broadCastMessage(clientUsername + " : " + messageFromClient);
        }

        if(messageFromClient.matches(Request.JOIN) ){

            //if name is not empty then the name is already set
            if(!clientUsername.isEmpty()){
                writeToClient("905 Name is already set");
                return;
            }
            String name=messageFromClient.replace("100 HELLO PLAYER ","");
            //checking the name length
            if(name.length()>30){
                writeToClient("901 Too large words in command");
                // checking if the name already exists
            }else if(Server.containsName(name)){
                writeToClient("902 This name is already used");
            }else{
                this.clientUsername=name;
                Server.clientHandlers.add(this);
                writeToClient("101 WELCOME "+name);
                System.out.println(name+" has successfully connected");
            }

        }else if(messageFromClient.matches(Request.CREATE_ROOM)){

            if(playerIsInARoom()){
                writeToClient("907 u are already in room");
                return;
            }
            String[] words=messageFromClient.substring(11).split("\\s*[a-zA-Z]+\\s+");
            int type=Integer.parseInt(words[0]);

            int numberOfPlayers=Integer.parseInt(words[1]);
            int minBet=Integer.parseInt(words[2]);
            int initialStack=Integer.parseInt(words[3]);

            if(type!=0 && type!=1) writeToClient("903 Incorrect value");
            else if( (type==0 && (numberOfPlayers<3 || numberOfPlayers>8)) || (type==1 && (numberOfPlayers<2 || numberOfPlayers>10))) writeToClient("903 Incorrect number of players");
            else if(minBet<=0) writeToClient("904 Incorrect minimal bet");
            else if(initialStack<= minBet*20) writeToClient("904 Incorrect stack");
            else{
                currentRoom=new SRoom(type,numberOfPlayers,minBet,initialStack);
                currentRoom.clientHandlers.add(this);
                Server.rooms.add(currentRoom);
                writeToClient("110 GAME CREATED "+currentRoom.getId());
            }


        }else if(messageFromClient.matches(Request.GET_ROOMS)){

            if(playerIsInARoom()){
                writeToClient("907 u are already in room");
                return;
            }
            writeToClient("120 NUMBER "+Server.rooms.size());
            for(int i=0;i< Server.rooms.size();i++){
                SRoom r=Server.rooms.get(i);
                writeToClient("121 MESS "+(i+1)+" ID "+r.getId()+" "+r.getType()+" "+r.getMaxPlayers()+" "+r.getMinBid()+" "+r.getInitStack()+" "+r.numberOfPlayers());
            }

        }else if(messageFromClient.matches(Request.JOIN_ROOM)){

            if(playerIsInARoom()){
                writeToClient("907 u are already in room");
                return;
            }
            int id=Integer.parseInt(messageFromClient.substring(9));
            for(SRoom room:Server.rooms){
                if(room.getId()==id ){
                    if(!room.hasRoomLeft() || room.startRequested() || room.gameStarted()) {
                        writeToClient("131 room uavailable");
                        return;
                    }else {
                        currentRoom=room;
                        currentRoom.clientHandlers.add(this);
                        this.stack=room.getInitStack();
                        writeToClient("131 GAME " + room.getId() + " JOINED");
                        broadCastMessage("140 " + clientUsername + " JOINED");
                        return;
                    }
                }
            }
            writeToClient("902 wrong id");

        }else if(messageFromClient.matches(Request.ACK_Player)){

                //there s nothing to do here
                //TODO

        }else if(messageFromClient.matches(Request.START_ROUND)){

            if(!playerIsInARoom()){
                writeToClient("907 u are not in a room");
                return;
            }else if(currentRoom.startRequested()){
                writeToClient("155 start already requested");
                return;
            }else if(currentRoom.gameStarted()){
                writeToClient("156 u are currently playing");
                return;
            }else if(!currentRoom.isAdmin(this.clientUsername)) {
                writeToClient("157 u are not the admin");
                return;
            }else if(!currentRoom.hasEnoughPlayersToStart()){
                writeToClient("158 not enough players");
                return;
            }else{
                //when a player initialize a start request does he say yes automatically___________________??????????????
                this.currentRoom.requestStart();
                broadCastMessageToEveryone("152 START REQUESTED");
            }

        }else if(messageFromClient.matches(Request.START_RESPONSE)){

            if(!playerIsInARoom() || !currentRoom.startRequested() ){
                writeToClient("158 there's no start request");
            }else{
                String response=messageFromClient.substring(10);
                if(response.equals("YES")) currentRoom.respond(this,true);
                else currentRoom.respond(this,false);

                if(currentRoom.allPlayersResponded()){
                    if(currentRoom.startApproved()){
                        currentRoom.setGameStarted(true);
                        broadCastMessageToEveryone("153 GAME STARTED");
                    }else {
                        ArrayList<String> playersWhoRefused= currentRoom.getPlayersWhoSaidNo();
                        System.out.println(playersWhoRefused.toString());
                        int k=playersWhoRefused.size();
                        broadCastMessageToEveryone("153 GAME ABORDED "+k);

                        int i=0;
                        for(;i<playersWhoRefused.size()/5;i++){
                            broadCastMessageToEveryone("154 MESS "+(i+1)+" PLAYER "
                                    +playersWhoRefused.get(i*5)+" "
                                    +playersWhoRefused.get(i*5+1)+" "
                                    +playersWhoRefused.get(i*5+2)+" "
                                    +playersWhoRefused.get(i*5+3)+" "
                                    +playersWhoRefused.get(i*5+4)
                            );
                        }
                        if(i*5<k){
                            broadCastMessageToEveryone("154 MESS "+(i+1)+" PLAYER "
                                    +playersWhoRefused.get(i*5)+" "
                                    +( (i*5+1<k)? playersWhoRefused.get(i+1)+" ":"")
                                    +( (i*5+2<k)? playersWhoRefused.get(i+2)+" ":"")
                                    +( (i*5+3<k)? playersWhoRefused.get(i+3):"")
                            );
                        }

                    }
                    currentRoom.abortStartRequested();
                }
            }

        }else if(messageFromClient.matches(Request.FOLD)){

            //Fonction chrono à ajouter
            if(!playerIsInARoom() || !currentRoom.gameStarted() || hasFolded){
                writeToClient("907 Invalid Command");
            }else {
                this.hasFolded = true;
                broadCastMessage("510 " + clientUsername + " FOLD");
                writeToClient("400 ACCEPTED");
                //Le joueur ne peut plus alors jouer mais assiste à la partie en cours
                //-> A chaque fin de tour (donc début d'une nouvelle partie) on ré-initialise tous les
                //hasFolded à false
            }


        }else if(messageFromClient.matches(Request.CHECK)){

            if(!playerIsInARoom() || !currentRoom.gameStarted() || hasFolded || currentPlayerBids<currentRoom.getHighestBid()){
                writeToClient("907 Invalid Command");
            }else{
                writeToClient("400 ACCEPTED");
                broadCastMessage("511 "+ clientUsername +" CHECK");
            }

        }else if(messageFromClient.matches(Request.CALL)){

            if(!playerIsInARoom() || !currentRoom.gameStarted() || hasFolded || currentPlayerBids>=currentRoom.getHighestBid() || this.stack<(currentRoom.getHighestBid()-currentPlayerBids) ) {
                writeToClient("907 Invalid Command");
            }else{
                this.stack=this.stack-(currentRoom.getHighestBid()-currentPlayerBids);
                this.currentPlayerBids = currentRoom.getHighestBid();
                writeToClient("400 ACCEPTED");
                broadCastMessage("512 "+ clientUsername +" CALL");
            }
                
        }else if(messageFromClient.matches(Request.RAISE)) {

            int raise = Integer.parseInt(messageFromClient.substring(10));
            if (!playerIsInARoom() || !currentRoom.gameStarted() || hasFolded || currentPlayerBids > currentRoom.getHighestBid() || this.stack < (currentRoom.getHighestBid() + raise - currentPlayerBids)) {
                writeToClient("907 Invalid Command");
            }
            this.stack = this.stack - (currentRoom.getHighestBid() + raise - currentPlayerBids);
            currentRoom.incrementHighestBid(raise);
            currentPlayerBids = currentRoom.getHighestBid();
            writeToClient("400 ACCEPTED");
            broadCastMessage("513 " + clientUsername + " RAISE " + raise);

        }else if(messageFromClient.matches(Request.ACTION_RECIEVED)){

            //Nothing here (probably)

        }else if(messageFromClient.matches(Request.CARDS_RECIEVED)){

            //Nothing here (probably)

        }else if(messageFromClient.matches(Request.CHANGE)){

            if(!playerIsInARoom() || !currentRoom.gameStarted() || currentRoom.getType()==1 || cards==null){
                writeToClient("999 ERROR");
                return;
            }
            String[] data=messageFromClient.split("\\s+");
            int numberOfCards=Integer.parseInt(data[2]);
            if(data.length!=numberOfCards+3){
                writeToClient("999 ERROR");
                return;
            }
            //TODO
            writeToClient("700 ACCEPTED");
            broadCastMessage("720 "+clientUsername+" Change "+numberOfCards);

        }else if(messageFromClient.matches(Request.CHANGE_RECIEVED)){

            //Nothing here (probably)

        }else if(messageFromClient.matches(Request.QUIT)){

            if(playerIsInARoom()) {
                if(currentRoom.startRequested()){
                    broadCastMessageToEveryone("153 GAME ABORDED "+1);
                    currentRoom.abortStartRequested();
                }
                writeToClient(Request.QUIT_ACCEPTED);
                broadCastMessage("211 " + currentRoom + " QUIT");
                currentRoom.clientHandlers.remove(this);
                currentRoom = null;
                this.stack=0;
            }

        }else if(messageFromClient.matches(Request.QUIT_RECIEVED)){

            //nothing to do here(probably)

        }else if(messageFromClient.matches(Request.ECHO)){

            writeToClient(messageFromClient.substring(9));

        }else{

            writeToClient("999 ERROR");//method is wrong

        }

    }


    private boolean playerIsInARoom(){return currentRoom!=null;}
    public String getClientUsername() {return clientUsername;}

}
//mutex java thread pour la partie serveur.
//when playe rleaves game he syas no!.
//can t join game when there S a start request.