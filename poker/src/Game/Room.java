package Game;

public abstract class Room {
    private static int COUNT=1;

    private boolean startRequested;
    private boolean gameStarted;

    private int type;
    private int id;
    private int minPlayers;
    private int minBid;
    private int initStack;

    private int tableMoney;
    private int highestBid;
    

    public Room(int id,int type, int minPlayers, int minBid, int initStack) {
        this.type = type;
        this.minPlayers = minPlayers;
        this.minBid = minBid;
        this.initStack = initStack;
        this.startRequested=false;
        this.gameStarted=false;
        this.id = id;
    }
    public Room(int type, int minPlayers, int minBid, int initStack) {
        this.type = type;
        this.minPlayers = minPlayers;
        this.minBid = minBid;
        this.initStack = initStack;
        this.startRequested=false;
        this.gameStarted=false;
        this.id=COUNT++;
    }

    public int getType() {return type;}
    public int getId() {return id;}
    public int getMinPlayers() {return minPlayers;}
    public int getMinBid() {return minBid;}
    public int getInitStack() {return initStack;}
    public void setId(int id) {this.id = id;}
    public int getHighestBid() {return highestBid;}
    public void incrementHighestBid(int raise) {this.highestBid += raise;}

    public boolean gameStarted() {return gameStarted;}
    public boolean startRequested(){return startRequested;}
    public void setStartRequested(boolean startRequested) {this.startRequested = startRequested;}
    public void setGameStarted(boolean gameStarted) {this.gameStarted = gameStarted;}

    public abstract boolean hasRoomLeft();
    public abstract int numberOfPlayers();
    public abstract boolean isAdmin(String userName);
}
