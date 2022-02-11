package Client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    private static final int PORT=1234;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public Client(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = "";
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
            writeToServer(messageToSend.trim());
        }
    }
    /*making a seperate thread for listening for messages that has been broadCasted*/
    public void listenForMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String comingMessage;

                while (socket.isConnected()){
                    try{
                        comingMessage=bufferedReader.readLine();
                        System.out.println(comingMessage);
                    }catch (IOException e){
                        closeEverything(socket,bufferedReader,bufferedWriter);
                    }
                }
            }
        }).start();
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


    public static void main(String[] args) {
        try {

            Socket socket = new Socket("localhost", PORT);
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
