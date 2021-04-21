package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SerdesTest {

    @Test
    void IntSerdeTest(){
        assertEquals("5", Serdes.INTEGER_SERDE.serialize(5));
        assertEquals(17, Serdes.INTEGER_SERDE.deserialize("17"));

        for(int i=0; i< 400; i++){
            assertEquals(Integer.toString(i), Serdes.INTEGER_SERDE.serialize(i));
            assertEquals(i, Serdes.INTEGER_SERDE.deserialize(Integer.toString(i)));
        }
    }

    @Test
    void StringSerdeTest(){
        assertEquals("U2FsdXQ=", Serdes.STRING_SERDE.serialize("Salut"));
        assertEquals("Salut", Serdes.STRING_SERDE.deserialize("U2FsdXQ="));

        assertEquals("Charles", Serdes.STRING_SERDE.deserialize("Q2hhcmxlcw=="));
        assertEquals("Q2hhcmxlcw==", Serdes.STRING_SERDE.serialize("Charles"));
    }

    @Test
    void PlayerIdSerdeTest(){
        for (PlayerId id : PlayerId.ALL){
            assertEquals(String.valueOf(id.ordinal()), Serdes.PLAYER_ID_SERDE.serialize(id));
            assertEquals(id, Serdes.PLAYER_ID_SERDE.deserialize(String.valueOf(id.ordinal())));
        }
    }

    @Test
    void TurnKindSerdeTest(){
        assertEquals("0", Serdes.TURNKIND_SERDE.serialize(Player.TurnKind.DRAW_TICKETS));
        assertEquals(Player.TurnKind.DRAW_CARDS, Serdes.TURNKIND_SERDE.deserialize("1"));

        for(Player.TurnKind turn : Player.TurnKind.ALL){
            assertEquals(String.valueOf(turn.ordinal()), Serdes.TURNKIND_SERDE.serialize(turn));
            assertEquals(turn, Serdes.TURNKIND_SERDE.deserialize(String.valueOf(turn.ordinal())));
        }
    }

    @Test
    void CardSerdeTest(){
        assertEquals("0", Serdes.CARD_SERDE.serialize(Card.BLACK));
        assertEquals(Card.VIOLET, Serdes.CARD_SERDE.deserialize("1"));

        for (Card card : Card.ALL){
            assertEquals(String.valueOf(card.ordinal()), Serdes.CARD_SERDE.serialize(card));
            assertEquals(card, Serdes.CARD_SERDE.deserialize(String.valueOf(card.ordinal())));
        }
    }

    @Test
    void RouteSerdeTest(){
        assertEquals("0", Serdes.ROUTE_SERDE.serialize(ChMap.routes().get(0)));
        assertEquals(ChMap.routes().get(1), Serdes.ROUTE_SERDE.deserialize("1"));

        for(Route route : ChMap.routes()){
            assertEquals(String.valueOf(ChMap.routes().indexOf(route)), Serdes.ROUTE_SERDE.serialize(route));
            assertEquals(route, Serdes.ROUTE_SERDE.deserialize(String.valueOf(ChMap.routes().indexOf(route))));
        }
    }

    @Test
    void TicketSerdeTest(){
        assertEquals("0", Serdes.TICKET_SERDE.serialize(ChMap.tickets().get(0)));
        assertEquals(ChMap.tickets().get(1), Serdes.TICKET_SERDE.deserialize("1"));

        for(Ticket ticket : ChMap.tickets()){
            assertEquals(String.valueOf(ChMap.tickets().indexOf(ticket)), Serdes.TICKET_SERDE.serialize(ticket));
            assertEquals(ticket, Serdes.TICKET_SERDE.deserialize(String.valueOf(ChMap.tickets().indexOf(ticket))));
        }
    }

    @Test
    void ListStringSerdeTest(){
        assertEquals("U2FsdXQ=,Q2hhcmxlcw==", Serdes.LIST_STRING_SERDE.serialize(List.of("Salut", "Charles")));
        assertEquals(List.of("Salut", "Charles"), Serdes.LIST_STRING_SERDE.deserialize("U2FsdXQ=,Q2hhcmxlcw=="));
    }

    @Test
    void ListCardSerdeTest(){
        assertEquals("0,1", Serdes.LIST_CARDS_SERDE.serialize(List.of(Card.BLACK, Card.VIOLET)));
        assertEquals(List.of(Card.BLUE, Card.GREEN), Serdes.LIST_CARDS_SERDE.deserialize("2,3"));

        for(int i=0; i<Card.COUNT; i++){
            assertEquals(
                    Card.ALL.subList(0, i).stream().map(c-> String.valueOf(c.ordinal())).collect(Collectors.joining(",")),
                    Serdes.LIST_CARDS_SERDE.serialize(Card.ALL.subList(0, i))
            );
            assertEquals(
                    Card.ALL.subList(0, i),
                    Serdes.LIST_CARDS_SERDE.deserialize(Card.ALL.subList(0, i).stream().map(c-> String.valueOf(c.ordinal())).collect(Collectors.joining(",")))
            );
        }

        assertEquals("", Serdes.LIST_CARDS_SERDE.serialize(List.of()));
        assertEquals(List.of(), Serdes.LIST_CARDS_SERDE.deserialize(""));
    }

    @Test
    void ListRouteSerdeTest(){
        assertEquals("0,1", Serdes.LIST_ROUTES_SERDE.serialize(ChMap.routes().subList(0, 2)));
        assertEquals(ChMap.routes().subList(2, 4), Serdes.LIST_ROUTES_SERDE.deserialize("2,3"));

        for(int i=0; i<ChMap.routes().size(); i++){
            assertEquals(
                    ChMap.routes().subList(0, i).stream().map(c-> String.valueOf(ChMap.routes().indexOf(c))).collect(Collectors.joining(",")),
                    Serdes.LIST_ROUTES_SERDE.serialize(ChMap.routes().subList(0, i))
            );
            assertEquals(
                    ChMap.routes().subList(0, i),
                    Serdes.LIST_ROUTES_SERDE.deserialize(ChMap.routes().subList(0, i).stream().map(c-> String.valueOf(ChMap.routes().indexOf(c))).collect(Collectors.joining(",")))
            );
        }

        assertEquals("", Serdes.LIST_ROUTES_SERDE.serialize(List.of()));
        assertEquals(List.of(), Serdes.LIST_ROUTES_SERDE.deserialize(""));
    }

    @Test
    void SortedBagCardSerdeTest(){
        assertEquals("0,1", Serdes.SORTEDBAG_CARDS_SERDE.serialize(SortedBag.of(1, Card.BLACK, 1, Card.VIOLET)));
        assertEquals(SortedBag.of(1, Card.BLUE, 1, Card.GREEN), Serdes.SORTEDBAG_CARDS_SERDE.deserialize("2,3"));

        for(int i=0; i<Card.COUNT; i++){
            assertEquals(
                    SortedBag.of(Card.ALL.subList(0, i)).stream().map(c-> String.valueOf(c.ordinal())).collect(Collectors.joining(",")),
                    Serdes.SORTEDBAG_CARDS_SERDE.serialize(SortedBag.of(Card.ALL.subList(0, i)))
            );
            assertEquals(
                    SortedBag.of(Card.ALL.subList(0, i)),
                    Serdes.SORTEDBAG_CARDS_SERDE.deserialize(SortedBag.of(Card.ALL.subList(0, i)).stream().map(c-> String.valueOf(c.ordinal())).collect(Collectors.joining(",")))
            );
        }

        assertEquals("", Serdes.SORTEDBAG_CARDS_SERDE.serialize(SortedBag.of()));
        assertEquals(SortedBag.of(), Serdes.SORTEDBAG_CARDS_SERDE.deserialize(""));
    }

    @Test
    void SortedBagTicketSerdeTest(){
        assertEquals("0,1", Serdes.SORTEDBAG_TICKETS_SERDE.serialize(SortedBag.of(ChMap.tickets().subList(0, 2))));
        assertEquals(SortedBag.of(ChMap.tickets().subList(2, 4)), Serdes.SORTEDBAG_TICKETS_SERDE.deserialize("2,3"));

        for(int i=0; i<ChMap.tickets().size(); i++){
            assertEquals(
                    SortedBag.of(ChMap.tickets().subList(0, i)).stream().map(c-> String.valueOf(ChMap.tickets().indexOf(c))).collect(Collectors.joining(",")),
                    Serdes.SORTEDBAG_TICKETS_SERDE.serialize(SortedBag.of(ChMap.tickets().subList(0, i)))
            );
            assertEquals(
                    SortedBag.of(ChMap.tickets().subList(0, i)),
                    Serdes.SORTEDBAG_TICKETS_SERDE.deserialize(SortedBag.of(ChMap.tickets().subList(0, i)).stream().map(c-> String.valueOf(ChMap.tickets().indexOf(c))).collect(Collectors.joining(",")))
            );
        }

        assertEquals("", Serdes.SORTEDBAG_TICKETS_SERDE.serialize(SortedBag.of()));
        assertEquals(SortedBag.of(), Serdes.SORTEDBAG_TICKETS_SERDE.deserialize(""));
    }

    @Test
    void ListSortedBagCardSerdeTest(){
        assertEquals("0,1;2,3", Serdes.LIST_SORTEDBAG_CARDS_SERDE.serialize(List.of(SortedBag.of(1, Card.BLACK, 1, Card.VIOLET), SortedBag.of(1, Card.BLUE, 1, Card.GREEN))));
        assertEquals(List.of(SortedBag.of(1, Card.BLACK, 1, Card.VIOLET), SortedBag.of(1, Card.BLUE, 1, Card.GREEN)), Serdes.LIST_SORTEDBAG_CARDS_SERDE.deserialize("0,1;2,3"));
    }

    @Test
    void PublicCardStateTest(){
        PublicCardState test = new PublicCardState(Card.ALL.subList(0, Constants.FACE_UP_CARDS_COUNT), 1, 2);

        assertEquals("0,1,2,3,4;1;2", Serdes.PUBLIC_CARD_STATE_SERDE.serialize(test));
        assertEqualsPublicCardState(test, Serdes.PUBLIC_CARD_STATE_SERDE.deserialize("0,1,2,3,4;1;2"));
    }

    @Test
    void PublicCardStateException(){
        assertThrows(NullPointerException.class, () -> Serdes.PUBLIC_CARD_STATE_SERDE.serialize(null));
        assertThrows(IllegalArgumentException.class, () -> Serdes.PUBLIC_CARD_STATE_SERDE.deserialize(""));
    }

    @Test
    void PublicPlayerStateTest(){
        PublicPlayerState test = new PublicPlayerState(1, 2, ChMap.routes().subList(0, 2));

        assertEquals("1;2;0,1", Serdes.PUBLIC_PLAYER_STATE_SERDE.serialize(test));
        assertEqualsPublicPlayerState(test, Serdes.PUBLIC_PLAYER_STATE_SERDE.deserialize("1;2;0,1"));
    }

    @Test
    void PublicPlayerStateException(){
        assertThrows(NullPointerException.class, () -> Serdes.PLAYER_STATE_SERDE.serialize(null));
        assertThrows(IllegalArgumentException.class, () -> Serdes.PLAYER_STATE_SERDE.deserialize(""));
    }

    @Test
    void PlayerStateTest(){
        PlayerState test = new PlayerState(SortedBag.of(ChMap.tickets().subList(0, 2)), SortedBag.of(Card.ALL.subList(0, 2)), ChMap.routes().subList(0, 2));

        assertEquals("0,1;0,1;0,1", Serdes.PLAYER_STATE_SERDE.serialize(test));
        assertEqualsPlayerState(test, Serdes.PLAYER_STATE_SERDE.deserialize("0,1;0,1;0,1"));
    }

    @Test
    void PlayerStateException(){
        assertThrows(NullPointerException.class, () -> Serdes.PLAYER_STATE_SERDE.serialize(null));
        assertThrows(IllegalArgumentException.class, () -> Serdes.PLAYER_STATE_SERDE.deserialize(""));
    }

    @Test
    void PublicGameState(){
        List<Card> fu = List.of(Card.RED, Card.WHITE, Card.BLUE, Card.BLACK, Card.RED);
        PublicCardState cs = new PublicCardState(fu, 30, 31);
        List<Route> rs1 = ChMap.routes().subList(0, 2);
        Map<PlayerId, PublicPlayerState> ps = Map.of(
                PlayerId.PLAYER_1, new PublicPlayerState(10, 11, rs1),
                PlayerId.PLAYER_2, new PublicPlayerState(20, 21, List.of()));
        PublicGameState gs =
                new PublicGameState(40, cs, PlayerId.PLAYER_2, ps, null);

        assertEquals("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:", Serdes.PUBLIC_GAME_STATE_SERDE.serialize(gs));

        assertEquals(gs.ticketsCount(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").ticketsCount());
        assertEqualsPublicCardState(gs.cardState(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").cardState());
        for (PlayerId id : PlayerId.ALL){
            assertEqualsPublicPlayerState(gs.playerState(id), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").playerState(id));
        }
        assertEquals(gs.lastPlayer(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").lastPlayer());
    }

    @Test
    void PublicGameStateException(){
        assertThrows(NullPointerException.class, () -> Serdes.PUBLIC_GAME_STATE_SERDE.serialize(null));
        assertThrows(IllegalArgumentException.class, () -> Serdes.PUBLIC_GAME_STATE_SERDE.deserialize(""));
    }

    private static void assertEqualsPublicCardState(PublicCardState pcs1, PublicCardState pcs2){
        assertEquals(pcs1.faceUpCards(), pcs2.faceUpCards());
        assertEquals(pcs1.deckSize(), pcs2.deckSize());
        assertEquals(pcs1.discardsSize(), pcs2.discardsSize());
    }

    private static void assertEqualsPublicPlayerState(PublicPlayerState pps1, PublicPlayerState pps2){
        assertEquals(pps1.ticketCount(), pps2.ticketCount());
        assertEquals(pps1.cardCount(), pps2.cardCount());
        assertEquals(pps1.routes(), pps2.routes());
    }

    private static void assertEqualsPlayerState(PlayerState ps1, PlayerState ps2){
        assertEqualsPublicPlayerState(ps1, ps2);
        assertEquals(ps1.tickets(), ps2.tickets());
        assertEquals(ps1.cards(), ps2.cards());
        assertEquals(ps1.routes(), ps2.routes());
    }
}
