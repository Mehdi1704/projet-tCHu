package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Affichage des messages d'information du déroulement de la partie.
 *
 * @author Mehdi Bouchoucha (314843)
 * @author Ali Ridha Mrad (314529)
 */
public final class Info {

    private final String playerName;

    public Info(String playerName) {

        this.playerName = playerName;

    }

    /**
     * Retourne le nom francais d'une ou plusieurs cartes de même couleur.
     *
     * @param card  carte a nommer
     * @param count nombre de cartes
     * @return nom francais de la carte
     */
    public static String cardName(Card card, int count) {

        switch (card) {
            case BLACK:
                return StringsFr.BLACK_CARD + StringsFr.plural(count);
            case VIOLET:
                return StringsFr.VIOLET_CARD + StringsFr.plural(count);
            case BLUE:
                return StringsFr.BLUE_CARD + StringsFr.plural(count);
            case GREEN:
                return StringsFr.GREEN_CARD + StringsFr.plural(count);
            case YELLOW:
                return StringsFr.YELLOW_CARD + StringsFr.plural(count);
            case ORANGE:
                return StringsFr.ORANGE_CARD + StringsFr.plural(count);
            case RED:
                return StringsFr.RED_CARD + StringsFr.plural(count);
            case WHITE:
                return StringsFr.WHITE_CARD + StringsFr.plural(count);
            case LOCOMOTIVE:
                return StringsFr.LOCOMOTIVE_CARD + StringsFr.plural(count);
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Annonce une egalité.
     *
     * @param playerNames noms des joueurs.
     * @param points      points des joueurs.
     * @return String "x et x sont ex æqo avec x points".
     */
    public static String draw(List<String> playerNames, int points) {

        return String.format(StringsFr.DRAW, String.join(StringsFr.AND_SEPARATOR, playerNames), points);
    }

    /**
     * Annonce le joueur qui jouera en premier.
     *
     * @return String "x jouera en premier".
     */
    public String willPlayFirst() {

        return String.format(StringsFr.WILL_PLAY_FIRST, playerName);
    }

    /**
     * Annonce que le joueur a gardé un nombre de billets.
     *
     * @param count nombre de tickets.
     * @return String " x a gardé x billets".
     */
    public String keptTickets(int count) {

        return String.format(StringsFr.KEPT_N_TICKETS, playerName, count, StringsFr.plural(count));
    }

    /**
     * Annonce le tour d'un joueur.
     *
     * @return String "C'est à x de jouer".
     */
    public String canPlay() {

        return String.format(StringsFr.CAN_PLAY, playerName);
    }

    /**
     * Annonce que le joueur a tiré un nombre de billets.
     *
     * @param count nombre de billets tirés.
     * @return String "x a tiré x billets".
     */
    public String drewTickets(int count) {

        return String.format(StringsFr.DREW_TICKETS, playerName, count, StringsFr.plural(count));
    }

    /**
     * Annonce qu'un joueur a tiré une carte de la pioche.
     *
     * @return String "x a tiré une carte de la pioche".
     */
    public String drewBlindCard() {

        return String.format(StringsFr.DREW_BLIND_CARD, playerName);
    }

    /**
     * Annonce qu'un joueur a tiré une carte visible.
     *
     * @param card carte tirée.
     * @return String "x a tiré une carte x visible".
     */
    public String drewVisibleCard(Card card) {

        return String.format(StringsFr.DREW_VISIBLE_CARD, playerName, cardName(card, 1));
    }

    /**
     * Annonce qu'une route a été prise au moyen de certaines cartes.
     *
     * @param route route prise.
     * @param cards cartes utilisées.
     * @return String "x a pris possession de la route...".
     */
    public String claimedRoute(Route route, SortedBag<Card> cards) {
        AudioPlayer.play("/ziw.wav",false);
        return String.format(StringsFr.CLAIMED_ROUTE, playerName, routeName(route), cardsNames(cards));
    }

    /**
     * Annonce une tentative de s'emparer d'un tunnel.
     *
     * @param route        route a prendre.
     * @param initialCards cartes utilisées.
     * @return String "x tente de s'emparer du tunnel...".
     */
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) {

        return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, playerName, routeName(route), cardsNames(initialCards));
    }

    /**
     * Annonce les cartes supplémentaires ainsi que l'existence, ou non d'un cout supplementaire.
     *
     * @param drawnCards     les cartes additionnelles.
     * @param additionalCost cout de cartes a ajouter.
     * @return String "Les cartes supplémentaires sont x...".
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost) {
        String additionalCostString = (additionalCost == 0)
                ? StringsFr.NO_ADDITIONAL_COST
                : String.format(StringsFr.SOME_ADDITIONAL_COST, additionalCost, StringsFr.plural(additionalCost));
        return String.format(StringsFr.ADDITIONAL_CARDS_ARE, cardsNames(drawnCards)) + additionalCostString;
    }

    /**
     * Annonce qu'un joueur ne s'est pas emparé de la route.
     *
     * @param route route qui n'a pas été prise.
     * @return String "x n'a pas pu (ou voulu) s'emparer de la route x".
     */
    public String didNotClaimRoute(Route route) {

        return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, playerName, routeName(route));
    }

    /**
     * Annonce le dernier tour du jeu.
     *
     * @param carCount nombre de wagons.
     * @return String "x n'a plus que x wagons, le dernier tour commence".
     */
    public String lastTurnBegins(int carCount) {

        return String.format(StringsFr.LAST_TURN_BEGINS, playerName, carCount, StringsFr.plural(carCount));
    }

    /**
     * Annonce le bonus donné au joueur ayant le plus long chemin.
     *
     * @param longestTrail plus long chemin.
     * @return String "x reçoit un bonus de 10 points pour le plus long trajet...".
     */
    public String getsLongestTrailBonus(Trail longestTrail) {

        return String.format(StringsFr.GETS_BONUS, playerName, Constants.LONGEST_TRAIL_BONUS_POINTS,
                longestTrail.station1().name() + StringsFr.EN_DASH_SEPARATOR + longestTrail.station2().name());
    }

    /**
     * Annonce le vainqueur et donne les points des joueurs.
     *
     * @param points      points du gagnant.
     * @param loserPoints points du perdant.
     * @return String "x remporte la victoire avec x points...".
     */
    public String won(int points, int loserPoints) {

        return String.format(StringsFr.WINS, playerName, points,
                StringsFr.plural(points), loserPoints, StringsFr.plural(loserPoints));
    }

    /**
     * Retourne le nom des stations de la route séparés par le caractère demandé.
     *
     * @param route route dont on veut le nom des stations.
     * @return station de départ et d'arrivée séparés par un tiret special.
     */
    private static String routeName(Route route) {

        return route.station1().name() + StringsFr.EN_DASH_SEPARATOR + route.station2().name();
    }

    /**
     * Retourne la chaine de caractères en français,
     * (separées de virgules et du séparateur AND pour le dernier terme)
     * representant les cartes donnéess en argument avec leur multiplicité.
     *
     * @param cards SortedBag de cartes à représenter en string.
     * @return String représentant les cartes ainsi que leur nombres.
     */
    private static String cardsNames(SortedBag<Card> cards) {

        ArrayList<String> listOfNames = new ArrayList<>();
        for (Card c : cards.toSet()) {
            int nbOfCards = cards.countOf(c);
            listOfNames.add(nbOfCards + " " + cardName(c, nbOfCards));
        }
        String cardCompleteName = String.join(", ", listOfNames.subList(0, listOfNames.size() - 1));
        String cardLastName = listOfNames.get(listOfNames.size() - 1);
        if (cards.toSet().size() == 1) {
            return cardLastName;
        } else {
            return cardCompleteName + StringsFr.AND_SEPARATOR + cardLastName;
        }
    }
}