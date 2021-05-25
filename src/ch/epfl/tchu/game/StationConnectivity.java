package ch.epfl.tchu.game;

/**
 * Interface de la connectivité de deux stations
 *
 * @author Mehdi Bouchoucha (314843)
 * @author Ali Ridha Mrad (314529)
 */
public interface StationConnectivity {
    boolean connected(Station s1, Station s2);

}
