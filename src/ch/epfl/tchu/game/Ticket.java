package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.gui.AudioPlayer;
import javafx.beans.property.SimpleStringProperty;

import java.util.List;
import java.util.TreeSet;

import static java.lang.Math.abs;

/**
 * Ticket de jeu
 *
 * @author Mehdi Bouchoucha (314843)
 * @author Ali Ridha Mrad (314529)
 */
public final class Ticket implements Comparable<Ticket> {

    private static boolean connect = false;
    private final List<Trip> trips;
    private final String text;
    //private  boolean connect;

    /**
     * constructeur pricipal
     *
     * @param trips liste de trajet
     * @throws IllegalArgumentException lève IllegalArgumentException si la liste de trajet est vide.
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
     * @param from   station de départ
     * @param to     station d'arrivée
     * @param points point correspondant
     */
    public Ticket(Station from, Station to, int points) {
        this(List.of(new Trip(from, to, points)));
    }


    /**
     * @param trips liste des trajets
     * @return une représentation textuelle du billet
     */
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
     * @return retourne la représentation textuelle du billet
     */
    public String text() {
        return (this.text);
    }

    /**
     * @param connectivity paramettre permettant de savoir si deux stations sont connectées
     * @return le nombre de points correspondant à la connectivité donnée
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

    public boolean ticketDone(StationConnectivity connectivity) {
        if (trips.size() == 1) {
            return connectivity.connected(trips.get(0).from(), trips.get(0).to());
        } else {
            for (Trip t : trips) {
                return (connectivity.connected(t.from(), t.to()));
            }
        }
        return connect;
    }

    /**
     * @return le texte
     */
    @Override
    public String toString() {
        return (this.text);
    }

    /**
     * qui compare le billet auquel on l'applique (this) à celui passé en argument (that)
     * par ordre alphabétique de leur représentation textuelle,
     *
     * @param that billet qu'on veut comparer à this .
     * @return un entier strictement négatif si this est strictement plus petit que that, un entier strictement
     * positif si this est strictement plus grand que that, et zéro si les deux sont égaux.
     */
    @Override
    public int compareTo(Ticket that) {
        return this.text().compareTo(that.text());
    }

}