package Server;

import Game.utils.Request;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

public class Server {

    private static HashSet<ClientHandler> clientHandlers=new HashSet<>();
    private static HashSet<Room> rooms=new HashSet<>();
    private static Server server;
    private ServerSocket serverSocket;


    private Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
    public static Server get(ServerSocket serverSocket){
        if(server==null) server= new Server(serverSocket);
        return server;
    }

    public void startServer(){
        try{
            while (!serverSocket.isClosed()){
                /*blocking method. Program will be halted here until a cliient connects?
                when a client connects, asocket object will be returned which can be used to communicate with the client*/
                Socket socket = serverSocket.accept();
                System.out.println("A new client is trying to connect");

                /*responsible for commmunicating with the client,
                implements runnable: its instances will be executed by a seperate thread*/
                ClientHandler clientHandler=new ClientHandler(socket);

                /*to spawn a new thread we need to create a thread object
                 and pass in our object that is an instance of the class that implements runnable*/
                Thread thread=new Thread(clientHandler);

                /*we use the start function to begin the execution of this thread*/
                thread.start();
            }
        }catch (IOException e){
            closeServerSocket();
        }
    }


    public void closeServerSocket(){
        try{
            if(serverSocket !=null) serverSocket.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try{
            ServerSocket serverSocket=new ServerSocket(Request.PORT);
            Server server=Server.get(serverSocket);
            server.startServer();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public static void createServer() {
        try{
            ServerSocket serverSocket=new ServerSocket(Request.PORT);
            Server server=Server.get(serverSocket);
            server.startServer();
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public static boolean containsName(String name){
        for(ClientHandler ch:clientHandlers){
            if(ch.getClientUsername().equals(name)) return true;
        }
        return false;
    }
    public static void addClient(ClientHandler ch){
        clientHandlers.add(ch);
    }
    public static void removeClient(ClientHandler ch){
        clientHandlers.remove(ch);
    }
    public static void addRoom(Room room){
        rooms.add(room);
    }
    public static void removeRoom(Room room){
        rooms.remove(room);
    }
    public static int numberOfRooms(){
        return rooms.size();
    }
    public static HashSet<Room> getRooms() {
        return rooms;
    }
    public static Room getRoom(int id) {
        for(Room room : rooms){
            if(room.getGame().getId()==id) return room;
        }
        return null;
    }
}

