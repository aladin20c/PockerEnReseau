package Server.ServerGameStates;

import Game.PokerFerme;
import Game.PokerGame;
import Game.TexasHoldem;
import Game.utils.Request;
import Server.ClientHandler;
import Server.Room;
import Server.Server;

import java.util.HashSet;

public class MenuState extends GameState{

    //the player doesnt have the list of rooms to connect to one of them only when he asks GETLIST
    private HashSet<Integer> clientRoomIds;

    public MenuState(ClientHandler clientHandler) {
        super(clientHandler);
        this.clientRoomIds=new HashSet<>();
    }


    @Override
    public void analyseRequest(String messageFromClient) {

        if(messageFromClient.matches(Request.CREATE_ROOM)){

            String[] words=messageFromClient.substring(11).split("\\s*[a-zA-Z]+\\s+");
            int type=Integer.parseInt(words[0]);
            int numberOfClients=Integer.parseInt(words[1]);
            int minBet=Integer.parseInt(words[2]);
            int initialStack=Integer.parseInt(words[3]);

            if(type!=0 && type!=1) {
                writeToClient(Request.INCORRECT_VALUE);
            } else if( (type==0 && (numberOfClients<3 || numberOfClients>8)) || (type==1 && (numberOfClients<2 || numberOfClients>10))) {
                writeToClient(Request.INCORRECT_PLAYERS);
            } else if(minBet<=0) {
                writeToClient(Request.INCORRECT_BET);
            } else if(initialStack<= minBet*20) {
                writeToClient(Request.INCORRECT_STACK);
            }else{
                PokerGame game= (type==1)? new TexasHoldem(type,numberOfClients,minBet,initialStack):new PokerFerme(type,numberOfClients,minBet,initialStack);
                this.room=new Room();
                room.setGame(game);
                room.addClient(clientHandler);
                Server.addRoom(room);
                writeToClient("111 GAME CREATED "+game.getId());
                clientHandler.setGameState(new WaitingState(clientHandler,room));
            }

        }else if(messageFromClient.matches(Request.GET_ROOMS)){

            writeToClient("120 NUMBER "+Server.numberOfRooms());
            this.clientRoomIds.clear();
            int index=1;
            for(Room room : Server.getRooms()){
                writeToClient(room.informationToString(index++));
                this.clientRoomIds.add(room.getGame().getId());
            }


        }else if(messageFromClient.matches(Request.JOIN_ROOM)){

            int id=Integer.parseInt(messageFromClient.substring(9));
            Room room=Server.getRoom(id);
            if(!clientRoomIds.contains(id) ||room==null|| !room.canAddNewClient()){
                writeToClient("132 room unavailable");
                return;
            }

            this.room=room;
            writeToClient("131 GAME " + room.getGame().getId() + " JOINED");
            broadCastTask(Request.ACK_Player);//(╯°□°)╯︵ ┻━┻
            broadCastMessage("140 " + clientHandler.getClientUsername() + " JOINED");

            writeToClient("155 LIST PLAYER "+room.numberOfClients());
            int index = 0;

            for (; index < room.numberOfClients() / 5; index++) {

                writeToClient("155 MESS " + (index + 1) + " PLAYER "
                        + room.getClientHandlers().get(index * 5).getClientUsername() + " "
                        + room.getClientHandlers().get(index * 5 + 1).getClientUsername() + " "
                        + room.getClientHandlers().get(index * 5 + 2).getClientUsername() + " "
                        + room.getClientHandlers().get(index * 5 + 3).getClientUsername() + " "
                        + room.getClientHandlers().get(index * 5 + 4).getClientUsername());
            }

            if (index * 5 < room.numberOfClients()) {
                writeToClient("155 MESS " + (index + 1) + " PLAYER "
                        + room.getClientHandlers().get(index * 5).getClientUsername() + " "
                        + ((index * 5 + 1 < room.numberOfClients()) ? room.getClientHandlers().get(index + 1).getClientUsername() + " " : "")
                        + ((index * 5 + 2 < room.numberOfClients()) ? room.getClientHandlers().get(index + 2).getClientUsername() + " " : "")
                        + ((index * 5 + 3 < room.numberOfClients()) ? room.getClientHandlers().get(index + 3).getClientUsername() : ""));
            }
            room.addClient(this.clientHandler);
            this.clientHandler.setGameState(new WaitingState(clientHandler,room));

        }else if(messageFromClient.matches(Request.GET_STATE)) {

            writeToClient("666 MenuState");

        }else {

            clientHandler.writeToClient(Request.ERROR);

        }
    }


}
