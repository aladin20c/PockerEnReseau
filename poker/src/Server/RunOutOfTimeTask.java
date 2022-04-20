package Server;


import java.util.TimerTask;

public class RunOutOfTimeTask extends TimerTask {

    ClientHandler clientHandler;
    String cancel;

    public RunOutOfTimeTask(ClientHandler clientHandler, String cancel) {
        this.clientHandler = clientHandler;
        this.cancel = cancel;
    }

    public boolean cancel(String string) {
        if(string.matches(cancel)) {
            return super.cancel();
        }
        return false;
    }

    @Override
    public void run() {
        System.out.println(clientHandler.getClientUsername()+" u should have hit "+cancel);//fixme remeove when done
        clientHandler.getGameState().clientQuit();
    }
}
