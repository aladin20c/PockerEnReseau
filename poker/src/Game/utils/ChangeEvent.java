package Game.utils;

import Game.Card;
import Game.Player;

public class ChangeEvent {

    public Player player;
    public int nbCards;
    public Card[] drawnCards;
    public Card[] discradedCards;

    public ChangeEvent(Player player, int nbCards) {
        this.player = player;
        this.nbCards = nbCards;
    }

    public ChangeEvent(Player player, int nbCards, Card[] drawnCards, Card[] discradedCards) {
        this(player, nbCards);
        this.drawnCards = drawnCards;
        this.discradedCards = discradedCards;
    }

    public void setDrawnCards(Card[] drawnCards) {
        this.drawnCards = drawnCards;
    }
    public void setDiscradedCards(Card[] discradedCards) {
        this.discradedCards = discradedCards;
    }

}
