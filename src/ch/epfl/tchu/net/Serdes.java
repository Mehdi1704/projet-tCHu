package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

/**
 *
 */
public final class Serdes {

    /**
     * Serde d'un entier
     */
    public static final Serde<Integer> INTEGER_SERDE = Serde.of(
            i -> Integer.toString(i),
            Integer::parseInt);

    /**
     * Serde d'un string
     */
    public static final Serde<String> STRING_SERDE = Serde.of(
            s -> Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8)),
            s -> new String(Base64.getDecoder().decode(s.getBytes(StandardCharsets.UTF_8))));

    /**
     * Serde d'un Player Id
     */
    public static final Serde<PlayerId> PLAYER_ID_SERDE = Serde.oneOf(PlayerId.ALL);

    /**
     * Serde d'un TurnKind de joueur
     */
    public static final Serde<Player.TurnKind> TURNKIND_SERDE = Serde.oneOf(Player.TurnKind.ALL);

    /**
     * Serde d'une carte
     */
    public static final Serde<Card> CARD_SERDE = Serde.oneOf(Card.ALL);

    /**
     * Serde d'une route
     */
    public static final Serde<Route> ROUTE_SERDE = Serde.oneOf(ChMap.routes());

    /**
     * Serde d'un ticket
     */
    public static final Serde<Ticket> TICKET_SERDE = Serde.oneOf(ChMap.tickets());

    /**
     * Serde d'une liste de string
     */
    public static final Serde<List<String>> LIST_STRING_SERDE = Serde.listOf(STRING_SERDE, ",");

    /**
     * Serde d'une liste de cartes
     */
    public static final Serde<List<Card>> LIST_CARDS_SERDE = Serde.listOf(CARD_SERDE, ",");

    /**
     * Serde d'une liste de routes
     */
    public static final Serde<List<Route>> LIST_ROUTES_SERDE = Serde.listOf(ROUTE_SERDE, ",");

    /**
     * Serde d'un SortedBag/Multiensemble de cartes
     */
    public static final Serde<SortedBag<Card>> SORTEDBAG_CARDS_SERDE = Serde.bagOf(CARD_SERDE, ",");

    /**
     * Serde d'un SortedBag/Multiensemble de tickets
     */
    public static final Serde<SortedBag<Ticket>> SORTEDBAG_TICKETS_SERDE = Serde.bagOf(TICKET_SERDE, ",");

    /**
     * Serde d'une liste de SortedBag/Multiensemble de cartes
     */
    public static final Serde<List<SortedBag<Card>>> LIST_SORTEDBAG_CARDS_SERDE = Serde.listOf(SORTEDBAG_CARDS_SERDE, ";");

    /**
     * Serde d'un Public Card State
     */
    public static final Serde<PublicCardState> PUBLIC_CARD_STATE_SERDE = Serde.of();

    /**
     * Serde d'un Public Player State
     */
    public static final Serde<PublicPlayerState> PUBLIC_PLAYER_STATE_SERDE = Serde.of();

    /**
     * Serde d'un Player State
     */
    public static final Serde<PlayerState> PLAYER_STATE_SERDE = Serde.of();

    /**
     * Serde d'un Public Game State
     */
    public static final Serde<PublicGameState> PUBLIC_GAME_STATE_SERDE = Serde.of();
}
