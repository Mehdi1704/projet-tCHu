package ch.epfl.tchu.net;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Serdes
 *
 * @author Mehdi Bouchoucha (314843)
 * @author Ali Ridha Mrad (314529)
 */
public final class Serdes {

    private Serdes() {
    }

    private static final char colon = ':';
    private static final char semicolon = ';';

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
     * Serde du Last Player
     */
    public static final Serde<PlayerId> LAST_PLAYER_SERDE = new Serde<>() {
        @Override
        public String serialize(PlayerId toSerialize) {
            return (toSerialize == null) ? "" : PLAYER_ID_SERDE.serialize(toSerialize);
        }

        @Override
        public PlayerId deserialize(String toDeserialize) {
            return (toDeserialize.equals("")) ? null : PLAYER_ID_SERDE.deserialize(toDeserialize);
        }
    };

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
    public static final Serde<PublicCardState> PUBLIC_CARD_STATE_SERDE = new Serde<>() {

        @Override
        public String serialize(PublicCardState toSerialize) {
            Objects.requireNonNull(toSerialize);
            return LIST_CARDS_SERDE.serialize(toSerialize.faceUpCards()) + semicolon +
                    INTEGER_SERDE.serialize(toSerialize.deckSize()) + semicolon +
                    INTEGER_SERDE.serialize(toSerialize.discardsSize());
        }

        @Override
        public PublicCardState deserialize(String toDeserialize) {
            Preconditions.checkIfEmptyString(toDeserialize);
            String[] position = toDeserialize.split(Pattern.quote(String.valueOf(semicolon)), -1);
            return new PublicCardState(
                    LIST_CARDS_SERDE.deserialize(position[0]),
                    INTEGER_SERDE.deserialize(position[1]),
                    INTEGER_SERDE.deserialize(position[2]));
        }
    };

    /**
     * Serde d'un Public Player State
     */
    public static final Serde<PublicPlayerState> PUBLIC_PLAYER_STATE_SERDE = new Serde<>() {

        @Override
        public String serialize(PublicPlayerState toSerialize) {
            Objects.requireNonNull(toSerialize);
            return INTEGER_SERDE.serialize(toSerialize.ticketCount()) + semicolon +
                    INTEGER_SERDE.serialize(toSerialize.cardCount()) + semicolon +
                    LIST_ROUTES_SERDE.serialize(toSerialize.routes());
        }

        @Override
        public PublicPlayerState deserialize(String toDeserialize) {
            Preconditions.checkIfEmptyString(toDeserialize);
            String[] position = toDeserialize.split(Pattern.quote(String.valueOf(semicolon)), -1);
            return new PublicPlayerState(
                    INTEGER_SERDE.deserialize(position[0]),
                    INTEGER_SERDE.deserialize(position[1]),
                    LIST_ROUTES_SERDE.deserialize(position[2]));
        }
    };

    /**
     * Serde d'un Player State
     */
    public static final Serde<PlayerState> PLAYER_STATE_SERDE = new Serde<>() {

        @Override
        public String serialize(PlayerState toSerialize) {
            Objects.requireNonNull(toSerialize);
            return SORTEDBAG_TICKETS_SERDE.serialize(toSerialize.tickets()) + semicolon +
                    SORTEDBAG_CARDS_SERDE.serialize(toSerialize.cards()) + semicolon +
                    LIST_ROUTES_SERDE.serialize(toSerialize.routes());
        }

        @Override
        public PlayerState deserialize(String toDeserialize) {
            Preconditions.checkIfEmptyString(toDeserialize);
            String[] position = toDeserialize.split(Pattern.quote(String.valueOf(semicolon)), -1);
            return new PlayerState(
                    SORTEDBAG_TICKETS_SERDE.deserialize(position[0]),
                    SORTEDBAG_CARDS_SERDE.deserialize(position[1]),
                    LIST_ROUTES_SERDE.deserialize(position[2]));

        }
    };

    /**
     * Serde d'un Public Game State
     */
    public static final Serde<PublicGameState> PUBLIC_GAME_STATE_SERDE = new Serde<>() {
        @Override
        public String serialize(PublicGameState toSerialize) {
            Objects.requireNonNull(toSerialize);
            return INTEGER_SERDE.serialize(toSerialize.ticketsCount()) + colon +
                    PUBLIC_CARD_STATE_SERDE.serialize(toSerialize.cardState()) + colon +
                    PLAYER_ID_SERDE.serialize(toSerialize.currentPlayerId()) + colon +
                    PUBLIC_PLAYER_STATE_SERDE.serialize(toSerialize.playerState(PlayerId.PLAYER_1)) + colon +
                    PUBLIC_PLAYER_STATE_SERDE.serialize(toSerialize.playerState(PlayerId.PLAYER_2)) + colon +
                    LAST_PLAYER_SERDE.serialize(toSerialize.lastPlayer());
        }

        @Override
        public PublicGameState deserialize(String toDeserialize) {
            Preconditions.checkIfEmptyString(toDeserialize);
            String[] position = toDeserialize.split(Pattern.quote(String.valueOf(colon)), -1);
            return new PublicGameState(
                    INTEGER_SERDE.deserialize(position[0]),
                    PUBLIC_CARD_STATE_SERDE.deserialize(position[1]),
                    PLAYER_ID_SERDE.deserialize(position[2]),
                    Map.of(PlayerId.PLAYER_1, PUBLIC_PLAYER_STATE_SERDE.deserialize(position[3]),
                            PlayerId.PLAYER_2, PUBLIC_PLAYER_STATE_SERDE.deserialize(position[4])),
                    LAST_PLAYER_SERDE.deserialize(position[5]));
        }
    };
}
