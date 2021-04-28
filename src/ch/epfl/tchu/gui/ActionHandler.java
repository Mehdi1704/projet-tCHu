package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

public interface ActionHandler {

    interface DrawTicketsHandler{
        void onDrawTickets();
    }

    interface DrawCardHandler{
        void onDrawCard(int index);
    }

    interface ClaimRouteHandler{
        void onClaimRoute(Route route, SortedBag<Card> cards);
    }

    interface ChooseTicketsHandler{
        void onChooseTickets(SortedBag<Ticket> tickets);
    }

    interface ChooseCardsHandler{
        void onChooseCards(SortedBag<Card> cards);
    }

}
