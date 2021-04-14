package ch.epfl.tchu.game;

import java.util.List;

/**
 *
 *
 * @author Mehdi Bouchoucha (314843)
 * @author Ali Ridha Mrad (314529)
 */
public enum PlayerId {

    PLAYER_1,
    PLAYER_2;

    public static final List<PlayerId> ALL = List.of(PlayerId.values());
    public static final int COUNT = ALL.size();

    /**
     *
     * @return le joueur suivant .
     */
    public PlayerId next() {

        return (this.equals(PLAYER_1) ? PLAYER_2 : PLAYER_1);

    }
}