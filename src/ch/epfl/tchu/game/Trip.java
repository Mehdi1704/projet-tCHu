package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Trip {

    private final Station from;
    private final Station to;
    private final int points;


    /**
     *
     * @param from station de depart
     * @param to station d'arrivée
     * @param points
     */
    public Trip(Station from, Station to, int points) {

        Preconditions.checkArgument(points > 0);
        this.points = points;
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);

    }

    /**
     *
     * @param from
     * @param to
     * @param points
     * @throws IllegalArgumentException
     * @return
     */
    public static List<Trip> all(List<Station> from, List<Station> to, int points) {
        ArrayList<Trip> trips = new ArrayList<>();
        Preconditions.checkArgument((from != null) && (to != null) && (points > 0));
        for (Station s : from) {
            for (Station v : to) {
                trips.add(new Trip(s, v, points));
            }
        }
        return trips;
    }

    /**
     * Getter de la station de depart
     *
     * @return station de depart
     */
    public Station from() {
        return this.from;
    }

    /**
     * Getter de la station d'arrivée
     *
     * @return station d'arrivée
     */
    public Station to() {
        return to;
    }

    /**
     *
     * @return
     */
    public int points() {
        return points;
    }

    /**
     *
     * @param connectivity
     * @return
     */
    public int points(StationConnectivity connectivity) {

        if (connectivity.connected(from(), to())) {
            return (points);
        } else return (-points);

    }


}