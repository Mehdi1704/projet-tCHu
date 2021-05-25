package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * ActionHandlers contient des coquilles de gestionnaires d'action
 * à éxecuter lorsque le joueur effectue une certaine action
 *
 * @author Mehdi Bouchoucha (314843)
 * @author Ali Ridha Mrad (314529)
 */
public interface ActionHandlers {

    @FunctionalInterface
    interface DrawTicketsHandler {
        /**
         * Methode appelée lorsque le joueur désire tirer des billets
         */
        void onDrawTickets();
    }

    @FunctionalInterface
    interface DrawCardHandler {
        /**
         * Methode appelée lorsque le joueur désire tirer une carte de l'emplacement donné
         *
         * @param index l'emplacement donné
         */
        void onDrawCard(int index);
    }

    @FunctionalInterface
    interface ClaimRouteHandler {
        /**
         * Methode appelée lorsque le joueur désire s'emparer
         * de la route donnée au moyen des cartes (initiales) données
         *
         * @param route route dont le joueur veut s'emparer
         * @param cards cartes dont le joueur veut se servir
         */
        void onClaimRoute(Route route, SortedBag<Card> cards);
    }

    @FunctionalInterface
    interface ChooseTicketsHandler {
        /**
         * Methode appelée lorsque le joueur a choisi de garder
         * les billets donnés suite à un tirage de billets
         *
         * @param tickets les billets que désire garder le joueur
         */
        void onChooseTickets(SortedBag<Ticket> tickets);
    }

    @FunctionalInterface
    interface ChooseCardsHandler {
        /**
         * Methode appelée lorsque le joueur a choisi d'utiliser
         * les cartes données comme cartes initiales ou additionnelles
         * lors de la prise de possession d'une route;
         * si cartes additionnelles, le multiensemble peut être vide
         *
         * @param cards cartes à utiliser
         */
        void onChooseCards(SortedBag<Card> cards);
    }

}
