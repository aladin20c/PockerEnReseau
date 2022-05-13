package Server;

import Server.ServerGameStates.GameState;
import Server.ServerGameStates.IdentificationState;


import java.io.*;
import java.net.Socket;
import java.util.*;


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

    //timer to manage TimerTasks
    private Timer timer;

    //tasks: when user replies the correspending commands, the corresponding task wil be cancelled otherwise it will kick the player from the game
    private HashSet<RunOutOfTimeTask> secondarySet;
    private Set<RunOutOfTimeTask> taskset;


    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.clientUsername="";
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.timer=new Timer();
            this.secondarySet=new HashSet<>();
            this.taskset = Collections.synchronizedSet(secondarySet);
            this.gameState=new IdentificationState(this);
        }catch(IOException e){
            closeEverything();
        }
    }

    public void writeToClient(String message){
        try{
            this.bufferedWriter.write(message);
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();
        }catch (IOException e){
            closeEverything();
        }
    }

    public void closeEverything(){
        removeClientHandler();
        try{
            //closing the outer wrapper will close the underlying streams (ex:outputStreamReader)
            if(bufferedReader!=null) bufferedReader.close();
            if(bufferedWriter!=null) bufferedWriter.close();
            if(timer!=null) timer.cancel();
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
                closeEverything();
                break;//when the client disconnects, we get out of the while loop
            }
        }
    }


    public BufferedWriter getBufferedWriter() {return bufferedWriter;}
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


    //task related methods
    public void addTask(String string){
        try {
            RunOutOfTimeTask task = new RunOutOfTimeTask(this, string);
            this.taskset.add(task);
            this.timer.schedule(task, 60_000);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void cancelTask(String string){
        try {
            taskset.removeIf(runOutOfTimeTask -> runOutOfTimeTask.cancel(string));
            timer.purge();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void purge(){
        try {
            taskset.removeIf(TimerTask::cancel);
            timer.purge();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
