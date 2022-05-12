package Main;

import Client.Client;
import Server.Server;

public class Main {
    public static void main( String[] args ) {
        boolean server = false;
        boolean client = false;
        boolean gui = false;
        if ( args.length == 0 ) {
            System.out.println("Pas assez d'arguments");
        } else {
            for ( int i=0; i<args.length; i++ ) {
                if(args[i].equals( "--server" )){
                    server = true;
                }
                else{
                    if(args[i].equals( "--client" )){
                            client = true;
                    }
                    if(args[i].equals( "--gui" )){
                        gui = true;
                    }
                }
            }
        }
        if(server){
            Server.createServer();
        }
        if(client){
            Client.createClient(gui);
        }
    }
}
