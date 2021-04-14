package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Chemin du jeu ; ensemble de routes connectant deux stations
 *
 * @author Mehdi Bouchoucha (314843)
 * @author Ali Ridha Mrad (314529)
 */
public final class Trail {
    private final Station station1;
    private final Station station2;
    private final List<Route> routes;
    private final int length;

    /**
     * Contructeur privé de notre classe
     *
     * @param station1 station de depart
     * @param station2 station d'arrivée
     * @param routes   liste de routes
     * @param length   longueur du chemin
     */
    private Trail(Station station1, Station station2, List<Route> routes, int length) {

        this.station1 = station1;
        this.station2 = station2;
        this.routes = routes;
        this.length = length;
    }

    /**
     * Methode retournant le chemin de routes le plus long
     *
     * @param routes Liste de route à analyser
     * @return Le chemin le plus long
     */
    public static Trail longest(List<Route> routes) {
        Trail longest = new Trail(null, null, List.of(), 0);

        if (routes.isEmpty()) return longest;

        List<Trail> trailList = new ArrayList<>();
        for (Route r : routes) {
            trailList.add(new Trail(r.station1(), r.station2(), List.of(r), r.length()));
            trailList.add(new Trail(r.station2(), r.station1(), List.of(r), r.length()));
        }

        while (!trailList.isEmpty()) {
            List<Trail> prolongedTrails = new ArrayList<>();
            for (Trail t : trailList) {

                for (Route r : routes) {
                    if (!t.routes.contains(r)) {
                        Trail.addTrail(t, r, prolongedTrails, r.station1(), r.station2());
                        Trail.addTrail(t, r, prolongedTrails, r.station2(), r.station1());
                    }
                }

                if (t.length > longest.length) longest = t;
            }
            trailList = prolongedTrails;
        }
        return longest;
    }


    /**
     * Methode permettant d'ajouter un chemin supplementaire à la liste de chemins
     *
     * @param t                     chemin a ajouter
     * @param r                     route a ajouter
     * @param prolongedTrails       liste de chemins a prolonger
     * @param correspondingStation1 pour verifier la compatibilité
     * @param correspondingStation2 marque la fin du chemin
     */
    private static void addTrail(Trail t, Route r, List<Trail> prolongedTrails, Station correspondingStation1, Station correspondingStation2) {
        if (t.station2.equals(correspondingStation1)) {
            ArrayList<Route> rToAdd = new ArrayList<>(t.routes);
            rToAdd.add(r);
            prolongedTrails.add(new Trail(t.station1(), correspondingStation2, rToAdd, t.length + r.length()));
        }
    }

    /**
     * Getter
     *
     * @return station 1
     */
    public Station station1() {
        return this.length() == 0 ? null : station1;
    }

    /**
     * Getter
     *
     * @return station 2
     */
    public Station station2() {
        return this.length() == 0 ? null : station2;
    }


    /**
     * Getter
     *
     * @return longueur du chemin
     */
    public int length() {
        return length;
    }

    /**
     * Affichage de notre plus long chemin
     *
     * @return String representant les stations par lequel passe le chemin et sa longueur
     */
    @Override
    public String toString() {
        ArrayList<String> stationNames = new ArrayList<>();
        Station currStation = station1;
        stationNames.add(currStation.name());
        for (Route route : routes) {
            currStation = route.stationOpposite(currStation);
            stationNames.add(currStation.name());
        }
        return String.format("%s (%d)", String.join(" - ", stationNames), length);
    }


}

