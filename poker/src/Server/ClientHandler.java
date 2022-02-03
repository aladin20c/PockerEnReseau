package Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
    public static ArrayList<ClientHandler> clientHandlers=new ArrayList<>();
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
                   clientHandler.bufferedWriter.flush();//___________________??????
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
        broadCastMessage(messageFromClient);
    }

}
