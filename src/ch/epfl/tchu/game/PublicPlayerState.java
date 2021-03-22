package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;

import static ch.epfl.tchu.game.Constants.INITIAL_CAR_COUNT;

public class PublicPlayerState {

    private final int ticketCount;
    private final int cardCount;
    private final List<Route> routes;
    private final int carCount;
    private final int claimPoints;

    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes){
        Preconditions.checkArgument(ticketCount >= 0 && cardCount >= 0);
        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        this.routes = List.copyOf(routes);

        int totalCars = INITIAL_CAR_COUNT;
        int totalPoints = 0;
        for (Route r : routes()) {
            totalCars -= r.length();
            totalPoints += r.claimPoints();
        }

        this.carCount = totalCars;
        this.claimPoints = totalPoints;
    }

    /**
     * Retourne le nombre de billets que possède le joueur
     *
     */
    public int ticketCount(){
        return ticketCount;
    }

    /**
     * Retourne le nombre de cartes que possède le joueur
     * @return
     */
    public int cardCount() {
        return cardCount;
    }

    /**
     * Retourne les routes dont le joueur s'est emparé
     * @return
     */
    public List<Route> routes() {
        return routes;
    }

    /**
     * Retourne le nombre de wagons que possède le joueur
     * @return
     */
    public int carCount(){
        return carCount;
    }

    /**
     * Retourne le nombre de points de construction obtenus par le joueur
     * @return
     */
    public int claimPoints(){
        return claimPoints;
    }
}
