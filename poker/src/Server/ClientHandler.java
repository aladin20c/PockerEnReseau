package Server;

import Game.Card;
import Game.Utils.Request;
import Server.ServerGameStates.GameState;
import Server.ServerGameStates.IdentificationState;


import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class ClientHandler implements Runnable{


    //used to establish a connection between the client and server
    private Socket socket;

    //used to read data (specifically messages ) that have been sent from the client
    private BufferedReader bufferedReader;

    //used to sent data (specifically messages ) to the client
    private BufferedWriter bufferedWriter;

    //the name of the client
    private String clientUsername;

    //the current state of the game(identification,menu,waiting,playing)
    private GameState gameState;



    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername="";
            this.gameState=new IdentificationState(this);

        }catch(IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
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
    public void broadCastMessage(String messageToSend, List<ClientHandler> clientHandlers){
        for (ClientHandler clientHandler : clientHandlers) {
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

    public void broadCastMessageToEveryone(String messageToSend, List<ClientHandler> clientHandlers){
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                clientHandler.bufferedWriter.write(messageToSend);
                clientHandler.bufferedWriter.newLine();
                clientHandler.bufferedWriter.flush();
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
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
        Server.removeClient(this);
        System.out.println("Server: "+clientUsername+" has left !");
        gameState.clientQuit();
    }

    @Override
    public void run(){
        /*everything here is run on a seperate thread
        we want here to listen for message which is a blocking operation: the program will be stuck until the operaton is completed*/

        String messageFromClient;
        while(socket.isConnected()){
            try{
                messageFromClient=bufferedReader.readLine();
                this.getGameState().analyseRequest(messageFromClient);
            }catch(Exception e){
                closeEverything(socket,bufferedReader,bufferedWriter);
                break;//when the client disconnects, we get out of the while loop
            }
        }
    }


    public String getClientUsername() {
        return clientUsername;
    }
    public void setClientUsername(String clientUsername) {
        this.clientUsername = clientUsername;
    }
    public GameState getGameState() {
        return gameState;
    }
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

}
//mutex java thread pour la partie serveur.
