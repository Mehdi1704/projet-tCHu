package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * Station du plateau de jeu
 *
 * @author Mehdi Bouchoucha (314843)
 * @author Ali Ridha Mrad (314529)
 */
public final class Station {
    private final int id;
    private final String name;

    /**
     * Constructeur public d'une station
     *
     * @param id   identificateur de la station
     * @param name le nom de la station
     * @throws IllegalArgumentException si l'identificateur de la station est négatif .
     */
    public Station(int id, String name) {
        Preconditions.checkArgument(id >= 0);
        this.id = id;
        this.name = name;
    }

    /**
     * Getter de l'id
     *
     * @return l'id (identification)
     */
    public int id() {
        return id;
    }

    /**
     * Getter du nom
     *
     * @return le nom
     */
    public String name() {
        return name;
    }

    /**
     * Donne le nom de la station en String
     *
     * @return le nom
     */
    @Override
    public String toString() {
        return name;
    }
}
