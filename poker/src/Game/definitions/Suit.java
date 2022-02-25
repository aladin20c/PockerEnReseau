package Game.definitions;

public enum Suit {
    CLUBS("c","Clubs"),
    DIAMONDS("d","Diamonds"),
    HEARTS("h","Hearts"),
    SPADES("s","Spades");

    //private fields
    private final String suitText;
    private final String shortName;

    //Constructor
    private Suit(String suitText,String shortName){
        this.suitText=suitText;
        this.shortName=shortName;
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
