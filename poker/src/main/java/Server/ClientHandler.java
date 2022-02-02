package Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{

    private static String JOIN_REQUEST="100 HELLO PLAYER .*";
    private static String CREATE_ROOM_REQUEST="CREATE \\d+ PLAYER \\d+ MIN \\d+ STACK \\d+";
    private static String GET_ROOMS_REQUEST="120 GETLIST";
    private static String JOIN_ROOM_REQUEST="130 JOIN \\d+";
    private static String ACK_REQUEST="141 .* ACK";
    private static String START_REQUEST="150 REQUEST START";
    private static String ACTION_RECIEVED="500 RECIEVED";
    private static String CARD_RECIEVED="600 RECIEVED";
    private static String CHANGE_REQUEST="710 CHANGE \\d+ ([DCST]\\d+\\s+)+";
    private static String CHANGE_RECIEVED="701 RECIEVED";
    private static String QUIT_REQUEST="210 QUIT";
    private static String QUIT_RECIEVED="201 RECIEVED";



    public static ArrayList<ClientHandler> clientHandlers=new ArrayList<>();


    //used to establish a connection between the client and server
    private Socket socket;

    //used to read data (specifically messages ) that have been sent from the client
    private BufferedReader bufferedReader;

    //used to sent data (specifically messages ) to the client
    private BufferedWriter bufferedWriter;

    private String clientUsername;

    private Room currentRoom;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);
            broadCastMessage("Server: "+clientUsername+" has joined!");
        }catch(IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    public void broadCastMessage(String messageToSend){
        for(ClientHandler clientHandler:clientHandlers){
            try{
                if(! clientHandler.clientUsername.equals(clientUsername)){
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            }catch(IOException e){
                closeEverything(socket,bufferedReader,bufferedWriter);
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
        clientHandlers.remove(this);
        broadCastMessage("Server: "+clientUsername+" has left!");
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

    public void analyseRequest(String request){
        if(request.equals("")) {

        }else{
            broadCastMessage(request);
        }
    }

}

