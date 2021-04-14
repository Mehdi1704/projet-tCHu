package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 *
 *
 * @author Mehdi Bouchoucha (314843)
 * @author Ali Ridha Mrad (314529)
 */
public final class Trip {

    private final Station from;
    private final Station to;
    private final int points;


    /**
     *
     * @param from station de depart
     * @param to station d'arrivée
     * @param points point correspondant au trajet .
     */
    public Trip(Station from, Station to, int points) {

        Preconditions.checkArgument(points > 0);
        this.points = points;
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);

    }

    /**
     *
     * @param from  liste des stations de depart
     * @param to liste des stations d'arrivée
     * @param points point correspondant au trajet.
     * @throws IllegalArgumentException lève IllegalArgumentException si la liste des stations de départ ou d'arrivée sont vide ou
     * si le nombre de points n'est pas strictement positif. .
     * @return retourne la liste de tous les trajets possibles allant d'une des gares de la première liste (from)
     * à l'une des gares de la seconde liste
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
     * @return le nombre de points du trajet
     */
    public int points() {
        return points;
    }

    /**
     *
     * @param connectivity paramettre permettant de savoir si deux stations sont connectées
     * @return le nombre de points du trajet pour la connectivité donnée.
     */
    public int points(StationConnectivity connectivity) {

        if (connectivity.connected(from(), to())) {
            return (points);
        } else return (-points);

    }


}