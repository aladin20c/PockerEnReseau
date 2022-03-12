package Server.ServerGameStates;

import Game.Utils.Request;
import Server.ClientHandler;
import Server.SRoom;
import Server.Server;

public class MenuState extends GameState{

    private String clientUsername;
    private boolean hasRoomsList;

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
            if(room==null|| !room.canAddNewPlayer()){
                writeToClient("131 room unavailable");
                return;
            }

            writeToClient("131 GAME " + room.getId() + " JOINED");
            broadCastMessage("141 " + clientUsername + " JOINED",room.getClientHandlers());



            writeToClient("155 LIST PLAYER "+room.numberOfPlayers());
            int i = 0;
            for (; i < room.numberOfPlayers() / 5; i++) {
                writeToClient("155 MESS " + (i + 1) + " PLAYER "
                        + room.getClientHandlers().get(i * 5) + " "
                        + room.getClientHandlers().get(i * 5 + 1) + " "
                        + room.getClientHandlers().get(i * 5 + 2) + " "
                        + room.getClientHandlers().get(i * 5 + 3) + " "
                        + room.getClientHandlers().get(i * 5 + 4));}
            if (i * 5 < room.numberOfPlayers()) {
                writeToClient("155 MESS " + (i + 1) + " PLAYER "
                        + room.getClientHandlers().get(i * 5) + " "
                        + ((i * 5 + 1 < room.numberOfPlayers()) ? room.getClientHandlers().get(i + 1) + " " : "")
                        + ((i * 5 + 2 < room.numberOfPlayers()) ? room.getClientHandlers().get(i + 2) + " " : "")
                        + ((i * 5 + 3 < room.numberOfPlayers()) ? room.getClientHandlers().get(i + 3) : ""));}

            room.addClient(this.clientHandler);
            this.clientHandler.setGameState(new WaitingState(clientHandler,clientUsername,room));

        }else {
            sendError();
        }
    }


}
