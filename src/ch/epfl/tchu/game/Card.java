package ch.epfl.tchu.game;

import java.util.List;

/**
 * Carte du jeu
 *
 * @author Mehdi Bouchoucha (314843)
 * @author Ali Ridha Mrad (314529)
 */
public enum Card {
    BLACK(Color.BLACK),
    VIOLET(Color.VIOLET),
    BLUE(Color.BLUE),
    GREEN(Color.GREEN),
    YELLOW(Color.YELLOW),
    ORANGE(Color.ORANGE),
    RED(Color.RED),
    WHITE(Color.WHITE),
    LOCOMOTIVE(null);

    private final Color color;
    public static final List<Card> ALL = List.of(Card.values());
    public static final int COUNT = ALL.size();
    public static final List<Card> CARS = List.of(BLACK, VIOLET, BLUE, GREEN, YELLOW, ORANGE, RED, WHITE);

    /**
     * @param color couleur à attribuer à la carte
     */
    Card(Color color) {
        this.color = color;
    }


    /**
     * @return la couleur du type de carte auquel on l'applique
     */
    public Color color() {
        return this.color;
    }

    /**
     * @param color couleur de la carte
     * @return retourne le type de carte wagon correspondant à la couleur donnée color.
     */
    public static Card of(Color color) {
        return Card.valueOf(color.name());
    }

}
