package Server.ServerGameStates;

import Game.Utils.Request;
import Server.ClientHandler;
import Server.SRoom;
import Server.Server;

public class MenuState extends GameState{

    private String clientUsername;
    private boolean hasRoomsList;//the player doesnt have the list of rooms to connect to one of them only when he asks GETLIST

    public MenuState(ClientHandler clientHandler, String clientUsername) {
        super(clientHandler, 1);
        this.clientUsername=clientUsername;
        this.hasRoomsList=false;
    }


    @Override
    public void analyseRequest(String messageFromClient) {

        if(messageFromClient.matches(Request.CREATE_ROOM)){

            String[] words=messageFromClient.substring(11).split("\\s*[a-zA-Z]+\\s+");
            int type=Integer.parseInt(words[0]);
            int numberOfPlayers=Integer.parseInt(words[1]);
            int minBet=Integer.parseInt(words[2]);
            int initialStack=Integer.parseInt(words[3]);

            if(type!=0 && type!=1) {
                writeToClient(Request.INCORRECT_VALUE);
            } else if( (type==0 && (numberOfPlayers<3 || numberOfPlayers>8)) || (type==1 && (numberOfPlayers<2 || numberOfPlayers>10))) {
                writeToClient(Request.INCORRECT_PLAYERS);
            } else if(minBet<=0) {
                writeToClient(Request.INCORRECT_BET);
            } else if(initialStack<= minBet*20) {
                writeToClient(Request.INCORRECT_STACK);
            } else{
                SRoom room=new SRoom(type,numberOfPlayers,minBet,initialStack);
                room.addClient(this.clientHandler);
                Server.addRoom(room);
                writeToClient("110 GAME CREATED "+room.getId());
                clientHandler.setGameState(new WaitingState(clientHandler,clientUsername,room));
            }

        }else if(messageFromClient.matches(Request.GET_ROOMS)){

            writeToClient("120 NUMBER "+Server.numberOfRooms());
            int counter=1;
            for(SRoom room : Server.getRooms()){
                writeToClient("121 MESS "+counter+" ID "+room.getId()+" "+room.getType()+" "+room.getMaxPlayers()+" "+room.getMinBid()+" "+room.getInitStack()+" "+room.numberOfPlayers());
                counter++;
            }
            this.hasRoomsList=true;

        }else if(messageFromClient.matches(Request.JOIN_ROOM)){

            int id=Integer.parseInt(messageFromClient.substring(9));
            SRoom room=Server.getRoom(id);
            if(!hasRoomsList ||room==null|| !room.canAddNewPlayer()){
                writeToClient("131 room unavailable");
                return;
            }

            writeToClient("131 GAME " + room.getId() + " JOINED");
            broadCastMessage("141 " + clientUsername + " JOINED",room.getClientHandlers());

            writeToClient("155 LIST PLAYER "+room.numberOfPlayers());
            int index = 0;

            for (; index < room.numberOfPlayers() / 5; index++) {

                writeToClient("155 MESS " + (index + 1) + " PLAYER "
                        + room.getClientHandlers().get(index * 5).getClientUsername() + " "
                        + room.getClientHandlers().get(index * 5 + 1).getClientUsername() + " "
                        + room.getClientHandlers().get(index * 5 + 2).getClientUsername() + " "
                        + room.getClientHandlers().get(index * 5 + 3).getClientUsername() + " "
                        + room.getClientHandlers().get(index * 5 + 4).getClientUsername());
            }

            if (index * 5 < room.numberOfPlayers()) {
                writeToClient("155 MESS " + (index + 1) + " PLAYER "
                        + room.getClientHandlers().get(index * 5).getClientUsername() + " "
                        + ((index * 5 + 1 < room.numberOfPlayers()) ? room.getClientHandlers().get(index + 1).getClientUsername() + " " : "")
                        + ((index * 5 + 2 < room.numberOfPlayers()) ? room.getClientHandlers().get(index + 2).getClientUsername() + " " : "")
                        + ((index * 5 + 3 < room.numberOfPlayers()) ? room.getClientHandlers().get(index + 3).getClientUsername() : ""));
            }

            room.addClient(this.clientHandler);
            this.clientHandler.setGameState(new WaitingState(clientHandler,clientUsername,room));

        }else {
            sendError();
        }
    }


}
