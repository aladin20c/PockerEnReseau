package Client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        }catch (IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    /*send messages to the clientHandler(the connection that the server has spawned to handle a client)*/
    public void sendMessage(){
        try{
            //the client handler constructor will be waiting for the client's username to be sent over
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner=new Scanner(System.in);
            while (socket.isConnected()){
                //get what the user is typing and sent it over
                String messageToSend=scanner.nextLine(); //when enter is pressed in the terminal, wht he typed will be captured here
                bufferedWriter.write(username+" : "+messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }catch (IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    /*making a seperate thread for listening for messages that has been broadCasted*/
    public void listenForMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFromGroupChat;

                while (socket.isConnected()){
                    try{
                        messageFromGroupChat=bufferedReader.readLine();
                        System.out.println(messageFromGroupChat);
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


    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter your username !!!");
            String username = scanner.nextLine();
            Socket socket = new Socket("localhost", 1234);
            Client client = new Client(socket, username);
            client.listenForMessage();
            client.sendMessage();
        }catch (IOException e){
            e.printStackTrace();
        }
    }









}
