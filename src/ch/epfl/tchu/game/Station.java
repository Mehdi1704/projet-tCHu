package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

public final class Station {
//TODO final?
    private final int id;
    private final String name;

    /**
     *
     * @throws IllegalArgumentException si l'identificateur de la station est négatif .
     * @param id identificateur de la station
     * @param name le nom de la station
     */
    public Station(int id, String name) {
        Preconditions.checkArgument(id >= 0);
        this.id = id;
        this.name = name;
    }

    /**
     *
     * @return l'id (identification)
     */
    public int id() {
        return id;
    }

    /**
     *
     * @return le nom
     */
    public String name() {
        return name;
    }

    /**
     *
     * @return  le nom
     */
    @Override
    public String toString() {
        return name;
    }
}
