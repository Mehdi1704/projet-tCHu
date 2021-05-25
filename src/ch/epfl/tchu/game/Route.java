package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ch.epfl.tchu.game.Constants.ADDITIONAL_TUNNEL_CARDS;

/**
 * Route du plateau de jeu
 *
 * @author Mehdi Bouchoucha (314843)
 * @author Ali Ridha Mrad (314529)
 */
public final class Route {
    /**
     * Niveau de la route
     */
    public enum Level {
        OVERGROUND,
        UNDERGROUND
    }

    private final String id;
    private final Station station1;
    private final Station station2;
    private final int length;
    private final Level level;
    private final Color color;

    /**
     * Constructeur public de route
     *
     * @param id       identificateur de la route
     * @param station1 station 1
     * @param station2 station2
     * @param length   taille de la route
     * @param level    niveau de la route (normal ou tunnel)
     * @param color    couleur de la route
     * @throws IllegalArgumentException lève IllegalArgumentException si on a une valeur qui n'est pas attendue
     * @throws NullPointerException     leve NullPointerException si on essaye d'utiliser null alors qu'un objet est necessaire
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color) {

        Preconditions.checkArgument((!(station1.equals(station2))) &&
                (length >= Constants.MIN_ROUTE_LENGTH) &&
                (length <= Constants.MAX_ROUTE_LENGTH));

        this.id = Objects.requireNonNull(id);
        this.station1 = Objects.requireNonNull(station1);
        this.station2 = Objects.requireNonNull(station2);
        this.length = length;
        this.level = Objects.requireNonNull(level);
        this.color = color;
    }

    /**
     * @return retourne l'identificateur de la route
     */
    public String id() {
        return id;
    }

    /**
     * @return retourne la station 1 de la route
     */
    public Station station1() {
        return station1;
    }

    /**
     * @return retourne la station 2 de la route
     */
    public Station station2() {
        return station2;
    }

    /**
     * @return retourne la taille de la route
     */
    public int length() {
        return length;
    }

    /**
     * @return retourne le niveau de la route
     */
    public Level level() {
        return level;
    }

    /**
     * @return retourne la couleur de la route
     */
    public Color color() {
        return color;
    }

    /**
     * methode retournant une liste composée de station1 et station2
     *
     * @return retourne une liste composée de station 1 et station 2
     */
    public List<Station> stations() {
        return (List.of(station1, station2));
    }

    /**
     * methode retournant la station opposée à celle qui est passé en argument.
     *
     * @param station station
     * @return la station opposée à celle qui est passé en argument ( station )
     * @throws IllegalArgumentException lève IllegalArgumentException si station n'est ni egale a station1 ou station2
     */
    public Station stationOpposite(Station station) {
        Preconditions.checkArgument(station.equals(station1) || station.equals(station2));
        return (station.equals(station1)) ? station2 : station1;
    }

    /**
     * retourne la liste de tous les ensembles de cartes qui pourraient être joués pour pouvoir
     * s'emparer de la route (tunnel)
     *
     * @return retourne la liste de tous les ensembles de cartes qui pourraient être joués
     * pour pouvoir s'emparer de la route (tunnel)
     */
    public List<SortedBag<Card>> possibleClaimCards() {

        ArrayList<SortedBag<Card>> possibleClaimCards = new ArrayList<>();

        if (level.equals(Level.OVERGROUND)) {
            if (color != null) possibleClaimCards.add(SortedBag.of(length, Card.of(color)));
            else Card.CARS.forEach(c -> possibleClaimCards.add(SortedBag.of(length, c)));
        } else {
            if (color != null) {
                for (int i = 0; i <= length; ++i)
                    possibleClaimCards.add(SortedBag.of(length - i, Card.of(color), i, Card.LOCOMOTIVE));
            } else {
                for (int i = 0; i <= length; ++i) {
                    if (i < length) {
                        for (Card c : Card.CARS)
                            possibleClaimCards.add(SortedBag.of(length - i, c, i, Card.LOCOMOTIVE));
                    } else {
                        possibleClaimCards.add(SortedBag.of(length, Card.LOCOMOTIVE));
                    }
                }
            }
        }
        return (List.copyOf(possibleClaimCards));
    }

    /**
     * retourne le nombre de carte additionnel à jouer pour pouvoir s'emparer de la route(tunnel)
     *
     * @param claimCards cartes qu'on va utiliser pour s'emparer de la route .
     * @param drawnCards cartes tirées de la pioche .(permettant de les comparer a claimCards )
     * @return le nombre de carte additionnel à jouer pour pouvoir s'emparer de la route(tunnel)
     * @throws IllegalArgumentException lève IllegalArgumentException si le nombre de cartes tirées
     *                                  de la pioches est différent de 3, et si la route n'est pas un tunnel
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards) {
        Preconditions.checkArgument((drawnCards.size() == ADDITIONAL_TUNNEL_CARDS) && level.equals(Level.UNDERGROUND));

        int count = 0;
        for (Card c : drawnCards) {
            if (claimCards.contains(c) || c.equals(Card.LOCOMOTIVE)) {
                count += 1;
            }
        }
        return count;
    }

    /**
     * retourne le nombre de points de construction associé a la longueur de la route .
     *
     * @return retourne le nombre de points de construction associé a la longueur de la route .
     */
    public int claimPoints() {
        return (Constants.ROUTE_CLAIM_POINTS.get(this.length));
    }

}

