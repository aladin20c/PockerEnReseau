package Client;

public class Player{


    protected String userName;
    protected int stack;
    protected int bids;
    protected boolean hasFolded;
    protected boolean dealer;
    protected boolean allIn;

    public Player(String userName, int stack) {
        this.userName = userName;
        this.stack = stack;
        this.bids=0;
        this.hasFolded=false;
        this.dealer=false;
        this.allIn=false;
    }

    public int getBids() {
        return bids;
    }

    public void setHasFolded(boolean hasFolded) {
        this.hasFolded = hasFolded;
    }
    public void setBids(int bids) {
        this.bids = bids;
    }
    public void changeStack(int change) {
        this.stack += change;
    }
}
