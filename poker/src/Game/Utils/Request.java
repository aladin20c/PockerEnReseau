package Game.Utils;

public class Request {

    public static final int PORT=1234;



    //from client to server
    public static String JOIN="100 HELLO PLAYER .+";
    //110 CREATE 0 PLAYER 5 MIN 5 STACK 10000
    public static String CREATE_ROOM="110 CREATE -?\\d+ PLAYER -?\\d+ MIN -?\\d+ STACK -?\\d+";
    public static String GET_ROOMS="120 GETLIST";
    public static String JOIN_ROOM="130 JOIN -?\\d+";
    public static String ACK_Player="141 .+ ACK";
    public static String START_ROUND="150 REQUEST START";
    public static String START_RESPONSE="152 START (YES|NO)";

    public static String ACTION_RECIEVED="500 RECIEVED";
    public static String CARDS_RECIEVED="600 RECIEVED";
    public static String CHANGE="710 CHANGE \\d+(\\s+[DCST](\\d|1[0123]))+";
    public static String CHANGE_RECIEVED="701 RECIEVED";


    public static String PLAYER_FOLD = "410 FOLD";
    public static String PLAYER_CALL="412 CALL";
    public static String PLAYER_CHECK = "411 CHECK";
    public static String PLAYER_RAISE = "413 RAISE \\d+";


    public static String QUIT="210 QUIT";
    public static String QUIT_RECIEVED="201 RECIEVED";
    public static String ECHO="000 echo .+";






    //from server to client
    public static String GAME_CREATED="110 GAME CREATED -?\\d+";
    public static String LIST_LENGTH="120 NUMBER -?\\d+";
    public static String ROOM_INFOS="121 MESS\\s+-?\\d+\\s+ID\\s+-?\\d+\\s+-?\\d+\\s+-?\\d+\\s+-?\\d+\\s+-?\\d+\\s+-?\\d+";
    public static String ROOM_JOINED="131 GAME -?\\d+ JOINED";
    public static String PLAYER_JOINED="141 .+ JOINED";
    public static String START_IS_REQUESTED="152 START REQUESTED";
    public static String GAME_STARTED="153 GAME STARTED";
    public static String GAME_ABORDED="154 START ABORDED \\d+";

    public static String QUIT_ACCEPTED="200 ACCEPTED";
    public static String PLAYER_QUIT="211 .+ QUIT";

}
