package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.TreeSet;

import static java.lang.Math.abs;

public final class Ticket implements Comparable<Ticket> {
//TODO constantes
    private final List<Trip> trips;
    private final String text;

    /**
     * constructeur pricipal
     *
     * @throws IllegalArgumentException
     * @param trips
     */
    public Ticket(List<Trip> trips) {
        Preconditions.checkArgument(!(trips.isEmpty()));
        for (Trip t : trips) {
            if (!(trips.get(0).from().name().equals(t.from().name()))) {
                throw new IllegalArgumentException();
            }
        }
        this.trips = List.copyOf(trips);
        this.text = computeText(trips);
    }

    /**
     * @param from
     * @param to
     * @param points
     */
    public Ticket(Station from, Station to, int points) {
        this(List.of(new Trip(from, to, points)));
    }


    /**
     * @param trips
     * @return
     */
    //TODO optimiser
    private static String computeText(List<Trip> trips) {
        if (trips.size() == 1) {
            return (String.format("%s - %s (%d)", trips.get(0).from(), trips.get(0).to(), trips.get(0).points()));
        } else {
            TreeSet<String> station = new TreeSet<>();
            for (Trip s : trips) {
                station.add(s.to().name() + " (" + s.points() + ")");

            }
            return (String.format("%s - {%s}", trips.get(0).from(), String.join(", ", station)));
        }
    }

    /**
     * @return
     */
    public String text() {
        return (this.text);
    }

    /**
     * @param connectivity
     * @return
     */
    public int points(StationConnectivity connectivity) {
        if (trips.size() == 1) {
            if (connectivity.connected(trips.get(0).from(), trips.get(0).to())) {
                return (trips.get(0).points());
            } else {
                return (-(trips.get(0).points()));
            }
        } else {
            int points = 0;
            boolean changed = false;
            for (Trip t : trips) {
                if (connectivity.connected(t.from(), t.to()) && (t.points() > points)) {
                    points = t.points();
                    changed = true;
                } else if (!(connectivity.connected(t.from(), t.to())) &&
                        (points == 0 || t.points() < abs(points)) && (!changed)) {
                    points = -t.points();
                }
            }
            return points;
        }
    }

    /**
     * @return
     */
    @Override
    public String toString() {
        return (text);
    }

    /**
     * @param that
     * @return
     */
    @Override
    public int compareTo(Ticket that) {
        return this.text().compareTo(that.text());
    }
}