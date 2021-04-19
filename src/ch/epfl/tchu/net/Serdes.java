package ch.epfl.tchu.net;

import ch.epfl.tchu.game.*;

import java.util.List;

public final class Serdes {
    public static final Serde<Integer> INTEGER = Serde.of(
            i -> Integer.toString(i),
            Integer::parseInt);

    public static final Serde<String> STRING = Serde.of(
            String::toString,
            String::toString);
    public static final Serde<PlayerId> PLAYER_ID = Serde.oneOf(PlayerId.ALL);

    public static final Serde<Player.TurnKind> TURNKIND = Serde.oneOf(Player.TurnKind.ALL);

    public static final Serde<Card> CARD = Serde.oneOf(Card.ALL);

    public static final Serde<Route> ROUTE = Serde.oneOf(ChMap.routes());

    public static final Serde<Ticket> TICKET = Serde.oneOf(ChMap.tickets());

    //public static final Serde<List<String>> LIST_STRING = Serde.listOf(List.of(STRING), ",");

    public static final Serde<List<Card>> LIST_CARDS = Serde.listOf(CARD, ",");

    public static final Serde<List<Route>> LIST_ROUTES = Serde.listOf(ROUTE, ",");
}
