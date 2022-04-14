package Client;

import Client.States.GameState;
import Client.States.IdentificationState;
import Game.utils.Request;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;


public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private GameState gameState;


    public Client(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //the client can now listen and write messages to server
            this.gameState=new IdentificationState(this);
            this.listenForMessage();
            this.sendMessage();
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
        this.gameState.analyseMessageToSend(messageToSend);
    }
    /*analysing coming messages before printing them */
    public void analyseComingMessage(String comingMessage) {
        this.gameState.analyseComingMessage(comingMessage);
    }


    public GameState getGameState() {
        return gameState;
    }
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }


    public static void main(String[] args) {
        try {

            Socket socket = new Socket("localhost", Request.PORT);
            Client client = new Client(socket);

        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
