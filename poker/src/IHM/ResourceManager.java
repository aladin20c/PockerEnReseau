package IHM;

import Game.Card;

import javax.swing.*;
import java.net.URL;

public class ResourceManager {
    private static final String IMAGE_PATH = "/images/card_%s.png";
    public static ImageIcon getCardImage(Card card) {
        int cardSuit=0;
        switch(card.getSuit().getShortName()){
            case "D":
                cardSuit=0;
                break;
            case "T":
                cardSuit=1;
                break;
            case "C":
                cardSuit=2;
                break;
            case "S":
                cardSuit=3;
                break;
        }
        int nbCard = cardSuit * 13 + card.getRank().getRank()-2;
        String nbCardString = String.valueOf(nbCard);
        if (nbCardString.length() == 1) {
            nbCardString = "0" + nbCardString;
        }
        String path = String.format(IMAGE_PATH, nbCardString);
        return getIcon(path);
    }

    public static ImageIcon getIcon(String path) {
        URL url = ResourceManager.class.getResource(path);
        if (url != null) {
            return new ImageIcon(url);
        } else {
            throw new RuntimeException("Resource file not found: " + path);
        }
    }
}
