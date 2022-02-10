package Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class ClientHandler implements Runnable{
    private static String JOIN_REQUEST="100 HELLO PLAYER .+";
    private static String CREATE_ROOM_REQUEST="110 CREATE -?\\d+ PLAYER -?\\d+ MIN -?\\d+ STACK -?\\d+";
    private static String GET_ROOMS_REQUEST="120 GETLIST";
    private static String JOIN_ROOM_REQUEST="130 JOIN -?\\d+";
    private static String ACK_Player="141 .+ ACK";
    private static String START_ROUND_REQUEST="150 REQUEST START";
    private static String ACTION_RECIEVED="500 RECIEVED";
    private static String CARD_RECIEVED="600 RECIEVED";
    private static String CHANGE_REQUEST="710 CHANGE \\d+(\\s+[DCST](\\d|1[0123]))+";
    private static String CHANGE_RECIEVED="701 RECIEVED";
    private static String QUIT_REQUEST="210 QUIT";
    private static String QUIT_RECIEVED="201 RECIEVED";

    private static String ECHO="000 echo .+";

    public static ArrayList<ClientHandler> clientHandlers=new ArrayList<>();

    private Room currentRoom;
    //used to establish a connection between the client and server
    private Socket socket;

    //used to read data (specifically messages ) that have been sent from the client
    private BufferedReader bufferedReader;

    //used to sent data (specifically messages ) to the client
    private BufferedWriter bufferedWriter;

    private String clientUsername;

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


            if(messageFromClient.matches(JOIN_REQUEST) ){

                //name can never be empty because it won t match
                if(!clientUsername.isEmpty()){
                    writeToClient("905 Name is already set");
                    return;
                }
                String name=messageFromClient.replace("100 HELLO PLAYER ","");
                if(name.length()>30){
                    writeToClient("901 Too large words in command");
                }else if(containsName(name)){
                    writeToClient("902 This name is already used");
                }else{
                    this.clientUsername=name;
                    clientHandlers.add(this);
                    writeToClient("101 WELCOME "+name);
                    System.out.println(name+" has successfully connected");
                }

            }else if(messageFromClient.matches(CREATE_ROOM_REQUEST)){

                String[] words=messageFromClient.substring(11).split("\\s*[a-zA-Z]+\\s+");
                int data[]=new int[words.length];
                writeToClient(Arrays.toString(words));
                for(int i=0;i<words.length;i++){
                    data[i]=Integer.parseInt(words[i]);
                }
                if(data[0]!=0 && data[0]!=1) writeToClient("903 Incorrect value");
                else if( (data[0]==0 && (data[1]<3 || data[1]>8)) || (data[0]==1 && (data[1]<2 || data[1]>10))) writeToClient("903 Incorrect number of players");
                else if(data[2]<=0) writeToClient("904 Incorrect minimal bet");
                else if(data[3]<= data[2]*20) writeToClient("904 Incorrect stack");
                else{
                    currentRoom=new Room(data[0],data[1],data[2],data[3]);
                    writeToClient("110 GAME CREATED id");
                }

            }else if(messageFromClient.matches(ECHO)){

                writeToClient(messageFromClient.substring(9));

            }else{

                writeToClient("999 ERROR");//method is wrong
            }
        }else {

            broadCastMessage(clientUsername+" : "+messageFromClient);

        }
    }


    public static boolean containsName(String name){
        for(ClientHandler ch:clientHandlers){
            if(ch.clientUsername.equals(name)) return true;
        }
        return false;
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

}
