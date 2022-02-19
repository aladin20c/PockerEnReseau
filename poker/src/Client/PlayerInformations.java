package Client;

public class PlayerInformations{


    protected String userName;
    protected int stack;
    protected boolean hasFolded;

    public PlayerInformations(String userName, int stack) {
        this.userName = userName;
        this.stack = stack;
    }

    public String getUserName() {
        return userName;
    }
    public int getStack() {return stack;}
    public void setStack(int stack) {this.stack = stack;}

}
