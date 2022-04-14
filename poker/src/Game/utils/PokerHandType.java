package Game.utils;

public enum PokerHandType {
    ROYAL_FLUSH(10,"Royal flush",5),
    STRAIGHT_FLUSH (9,"Straight flush",5),
    FOUR_OF_A_KIND(8,"Four of a kind",4),
    FULL_HOUSE(7,"Full house",5),
    FLUSH(6,"Flush",5),
    STRAIGHT(5,"Straight",5),
    THREE_OF_A_KIND(4,"Three of a kind",3),
    TWO_PAIRS(3,"Two pair",4),
    ONE_PAIR(2,"One pair",2),
    HIGH_CARD(1,"High card",1),
    NOTHING(0,"No hand",0);

    private final int power;
    private final String name;
    private final int nbCardsRequired;

    private PokerHandType(int power, String  name, int nbCardsRequired){
        this.power=power;
        this.name=name;
        this.nbCardsRequired=nbCardsRequired;
    }

    public int getPower(){
        return power;
    }
    public String getName(){
        return name;
    }
    public int getNbCardsRequired(){
        return nbCardsRequired;
    }



}
