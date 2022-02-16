package Client;

public class PlayerInformations{


    protected String userName;
    protected int stack;


    public PlayerInformations(String userName, int stack) {
        this.userName = userName;
        this.stack = stack;
    }

    public String getUserName() {
        return userName;
    }
}
