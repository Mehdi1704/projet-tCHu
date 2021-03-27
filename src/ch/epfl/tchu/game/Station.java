package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

public final class Station {

    private int id;
    private String name;

    /**
     *
     * @param id
     * @param name
     */
    public Station(int id, String name) {
        Preconditions.checkArgument(id >= 0);
        this.id = id;
        this.name = name;
    }

    /**
     *
     * @return
     */
    public int id() {
        return id;
    }

    /**
     *
     * @return
     */
    public String name() {
        return name;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return name;
    }
}
