package Game;

public enum Suit {
    HEARTS("Hearts"),
    SPADES("Spades"),
    DIAMONDS("Diamonds"),
    CLUBS("Clubs");

    //private fields
    private final String suitText;

    //Constructor
    private Suit(String suitText){
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
}
