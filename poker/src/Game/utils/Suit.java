package Game.utils;

public enum Suit {
    CLUBS("T",0),//Clubs
    DIAMONDS("D",1),//Diamonds
    HEARTS("C",2),//Hearts
    SPADES("S",3);//Spades

    //private fields
    private final String shortName;
    private final int rank;

    //Constructor
    private Suit(String shortName,int rank){
        this.shortName=shortName;
        this.rank=rank;
    }

    //Public methods

    public int getRank() {
        return rank;
    }

    /**
     * To get the suit
     * @return
     */

    public String getShortName(){
        return shortName;
    }
}
