package Game.simulator;
public class Data{
    int tries;
    double time;
    double ahead;
    double tied;
    double behind;

    public Data() {
    }

    public Data(int tries,double time, double ahead, double tied, double behind) {
        this.tries=tries;
        this.time = time;
        this.ahead = ahead;
        this.tied = tied;
        this.behind = behind;
    }

    @Override
    public String toString() {
        return "Data{" +
                "tries=" + tries +
                ", time=" + time +
                ", ahead=" + ahead +
                ", tied=" + tied +
                ", behind=" + behind +
                '}';
    }
}
