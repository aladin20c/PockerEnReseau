package Game;

public abstract class Room {

    private int id;
    private int type;
    private int maxPlayers;
    private int minBid;
    private int initStack;

    private int highestBid;
    private int tableMoney;


    public Room(int id, int type, int maxPlayers, int minBid, int initStack) {
        this.id = id;
        this.type = type;
        this.maxPlayers = maxPlayers;
        this.minBid = minBid;
        this.initStack = initStack;
        this.highestBid=0;
        this.tableMoney=0;
    }



    public int getId() {return id;}
    public int getType() {return type;}
    public int getMaxPlayers() {return maxPlayers;}
    public int getMinBid() {return minBid;}
    public int getInitStack() {return initStack;}
    public int getHighestBid() {return highestBid;}
    public int getTableMoney() {return tableMoney;}


    public void incrementHighestBid(int raise) {this.highestBid += raise;}
    public void setId(int id) {this.id = id;}
}
