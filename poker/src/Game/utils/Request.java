package Game.utils;

public class Request {

    public static final int PORT=1234;

    //identification
    public static String JOIN="100 HELLO PLAYER .+";
    public static String WELCOME="101 WELCOME .+";
    public static String LARGE_NAME="901 Too large words in command";
    public static String USED_NAME="902 This name is already used";

    //menu
    public static String CREATE_ROOM="110 CREATE -?\\d+ PLAYER -?\\d+ MIN -?\\d+ STACK -?\\d+";//110 CREATE 1 PLAYER 2 MIN 10 STACK 10000
    public static String INCORRECT_VALUE="903 Incorrect value";//110 CREATE 0 PLAYER 3 MIN 10 STACK 10000
    public static String INCORRECT_PLAYERS="904 Incorrect number of players";
    public static String INCORRECT_BET="905 Incorrect minimal bet";
    public static String INCORRECT_STACK="906 Incorrect stack";
    public static String GAME_CREATED="111 GAME CREATED -?\\d+";

    public static String GET_ROOMS="120 GETLIST";
    public static String LIST_LENGTH="120 NUMBER -?\\d+";
    public static String ROOM_INFOS="121 MESS\\s+-?\\d+\\s+ID\\s+-?\\d+\\s+-?\\d+\\s+-?\\d+\\s+-?\\d+\\s+-?\\d+\\s+-?\\d+";

    public static String JOIN_ROOM="130 JOIN -?\\d+";// 130 JOIN 1
    public static String ROOM_JOINED="131 GAME -?\\d+ JOINED";



    //waiting
    public static String PLAYERS_LIST="155 LIST PLAYER \\d";
    public static String PLAYERS_INFOS="155 MESS \\d+ PLAYER((\\s+.+){1,5})";

    public static String PLAYER_JOINED="140 .+ JOINED";
    public static String ACK_Player="141 .+ ACK";

    public static String START_ROUND="150 REQUEST START";
    public static String START_IS_REQUESTED="152 START REQUESTED";
    public static String START_RESPONSE="152 START (YES|NO)";//152 START YES  ||  152 START NO

    public static String GAME_STARTED="153 GAME STARTED";
    public static String GAME_ABORDED="154 START ABORDED \\d+";

    //playing
    public static String FOLD = "410 FOLD";
    public static String CHECK = "411 CHECK";
    public static String CALL="412 CALL";
    public static String RAISE = "413 RAISE \\d+";//  413 RAISE 10
    public static String ACTION_ACCEPTED="400 ACCEPTED";


    public static String PLAYER_FOLD = "510 .+ FOLD";
    public static String PLAYER_CHECK = "511 .+ CHECK";
    public static String PLAYER_CALL="512 .+ CALL";
    public static String PLAYER_RAISE = "513 .+ RAISE \\d+";

    public static String ACTION_RECIEVED="500 RECIEVED";


    public static String CARDS_DISTRIBUTION="610 CARDS \\d+(\\s[DCST][0-9]+)*";
    public static String CARDS_RECIEVED="600 RECIEVED";


    public static String CHANGE="710 CHANGE \\d+(\\s[DCST][0-9]+)*";
    public static String CHANGE_ACCEPTED="700 ACCEPTED";
    public static String PLAYER_CHANGED_CARDS="720 .+ CHANGE \\d+";
    public static String CHANGE_RECIEVED="701 RECIEVED";


    public static String WINNERS="810 .+ WIN";
    public static String WINNERSANDCARDS="810 .+ \\d+ HAS.*";
    public static String WINRECEIVED="800 RECEIVED";



    //general
    public static String QUIT="210 QUIT";
    public static String QUIT_ACCEPTED="200 ACCEPTED";

    public static String PLAYER_QUIT="211 .+ QUIT";
    public static String QUIT_RECIEVED="201 RECIEVED";

    public static String ERROR="999 ERROR";
    public static String INVALID="907 Invalid Command";

    //Cheats
    public static String GET_STATE="777 GET STATE";
    public static String GET_ALL_CARDS="777 GET ALLCARDS";
    public static String GET_ALL_PLAYERS="777 GET ALLPLAYERS";
    public static String GET_ACTIVE_PLAYERS="777 GET ACTIVE PLAYERS";
    public static String GET_QUITTED_PLAYERS="777 GET QUITTED PLAYERS";
    public static String GET_FOLDED_PLAYERS="777 GET FOLDED PLAYERS";

    public static String STATE="666 .+State";
    public static String ALL_PLAYERS="666 \\d+ ALLPLAYERS.*";
    public static String ACTIVE_PLAYERS="666 \\d+ ACTIVEPLAYERS.*";
    public static String QUITTED_PLAYERS="666 \\d+ QUITTEDPLAYERS.*";
    public static String FOLDED_PLAYERS="666 \\d+ FOLDEDPLAYERS.*";
}
