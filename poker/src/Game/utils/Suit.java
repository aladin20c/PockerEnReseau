package Game.utils;

public enum Suit {
    CLUBS("T","Clubs"),
    DIAMONDS("D","Diamonds"),
    HEARTS("C","Hearts"),
    SPADES("S","Spades");

    //private fields
    private final String shortName;
    private final String suitText;

    //Constructor
    private Suit(String shortName,String suitText){
        this.shortName=shortName;
        this.suitText=suitText;
    }

    //Public methods

    /**
     * To get the suit
     * @return
     */
    public String printSuit(){
        return suitText;
    }
    public String getShortName(){
        return shortName;
    }
}
