package Server;

import Game.Utils.Request;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

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

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            clientUsername="";
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
        if(playerIsInARoom()) currentRoom.clientHandlers.remove(this);
        broadCastMessage("Server: "+clientUsername+" has left !");
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
            }catch(IOException e){
                closeEverything(socket,bufferedReader,bufferedWriter);
                break;//when the client disconnects, we get out of the while loop
            }
        }
    }




    public void analyseRequest(String messageFromClient){
        //if a command begins with three numbers, it is considered as a command, else it is considered as a chat function
        if(messageFromClient.matches("\\d\\d\\d.*")){


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
                writeToClient("121 NUMBER "+Server.rooms.size());
                for(int i=0;i< Server.rooms.size();i++){
                    SRoom r=Server.rooms.get(i);
                    writeToClient("121 MESS "+(i+1)+" "+r.getId()+" "+r.getType()+" "+r.getMinPlayers()+" "+r.getMinBid()+" "+r.getInitStack()+" "+r.numberOfPlayers());
                }

            }else if(messageFromClient.matches(Request.JOIN_ROOM)){

                if(playerIsInARoom()){
                    writeToClient("907 u are already in room");
                    return;
                }
                int id=Integer.parseInt(messageFromClient.substring(9));
                for(SRoom room:Server.rooms){
                    if(room.getId()==id ){
                        if(!room.hasRoomLeft()){
                            writeToClient("131 room is full");
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
                }else if(isPlaying()){
                    writeToClient("156 u are currently playing");
                    return;
                }else if(!currentRoom.isAdmin(this.clientUsername)){
                    writeToClient("157 u are not the admin");
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

            }else if(messageFromClient.matches(Request.ECHO)){
                writeToClient(messageFromClient.substring(9));
            }else{
                writeToClient("999 ERROR");//method is wrong
            }
        }else {
            broadCastMessage(clientUsername+" : "+messageFromClient);
        }
    }








    private boolean playerIsInARoom(){return currentRoom!=null;}
    public boolean isPlaying() {return currentRoom!=null && currentRoom.gameStarted();}
    public String getClientUsername() {return clientUsername;}

}
