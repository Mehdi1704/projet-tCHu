package ch.epfl.tchu.game;

import java.util.List;

/**
 * Identité du joueur
 *
 * @author Mehdi Bouchoucha (314843)
 * @author Ali Ridha Mrad (314529)
 */
public enum PlayerId {

    PLAYER_1,
    PLAYER_2;

    public static final List<PlayerId> ALL = List.of(values());
    public static final int COUNT = ALL.size();

    /**
     * Donne le joueur suivant
     *
     * @return le joueur suivant
     */
    public PlayerId next() {

        return values()[(this.ordinal() + 1) % COUNT];
    }
}