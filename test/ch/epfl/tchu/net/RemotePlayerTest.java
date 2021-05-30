package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Not really a comment
 */
final class storage {

    public final static Map<PlayerId, String> playerNames = Map.of(PlayerId.PLAYER_1, "Ada", PlayerId.PLAYER_2, "Charles");
    public final static String receiveInfo = "info to proxy";
    public final static SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().subList(0, 5));
    public final static SortedBag<Card> cards = SortedBag.of(2, Card.BLACK, 2, Card.RED);
    public final static List<Route> routes = ChMap.routes().subList(0, 5);
    public final static Route route = ChMap.routes().get(10);
    public final static Player.TurnKind nextTurn = Player.TurnKind.CLAIM_ROUTE;
    public final static PlayerId id = PlayerId.PLAYER_1;
    public final static int drawSlot = 3;

    public final static PublicCardState cs = new PublicCardState(
            List.of(Card.RED, Card.WHITE, Card.BLUE, Card.BLACK, Card.RED), 30, 31);

    public final static Map<PlayerId, PublicPlayerState> pps = Map.of(
            PlayerId.PLAYER_1, new PublicPlayerState(10, 11, ChMap.routes().subList(0, 2)),
            PlayerId.PLAYER_2, new PublicPlayerState(20, 21, List.of()));

    public final static PublicGameState gs = new PublicGameState(40, cs, PlayerId.PLAYER_2, pps, null);
    public final static PlayerState ps = new PlayerState(SortedBag.of(ChMap.tickets().subList(0, 5)), cards, routes);

    public static void assertEqualsPublicCardState(PublicCardState pcs1, PublicCardState pcs2) {
        assertEquals(pcs1.faceUpCards(), pcs2.faceUpCards());
        assertEquals(pcs1.deckSize(), pcs2.deckSize());
        assertEquals(pcs1.discardsSize(), pcs2.discardsSize());
    }

    public static void assertEqualsPublicPlayerState(PublicPlayerState pps1, PublicPlayerState pps2) {
        assertEquals(pps1.ticketCount(), pps2.ticketCount());
        assertEquals(pps1.cardCount(), pps2.cardCount());
        assertEquals(pps1.routes(), pps2.routes());
    }

    public static void assertEqualsPlayerState(PlayerState ps1, PlayerState ps2) {
        assertEqualsPublicPlayerState(ps1, ps2);
        assertEquals(ps1.tickets(), ps2.tickets());
        assertEquals(ps1.cards(), ps2.cards());
        assertEquals(ps1.routes(), ps2.routes());
    }

    public static void assertEqualsPublicGameState(PublicGameState gs1, PublicGameState gs2) {
        assertEquals(gs1.ticketsCount(), gs2.ticketsCount());
        assertEqualsPublicCardState(gs1.cardState(), gs2.cardState());
        PlayerId.ALL.forEach(id -> assertEqualsPublicPlayerState(gs1.playerState(id), gs2.playerState(id)));
        assertEquals(gs1.lastPlayer(), gs2.lastPlayer());
    }
}



final class TestServer {

    public static void main(String[] args) throws IOException {
        System.out.println("Starting server!");
        try (ServerSocket serverSocket = new ServerSocket(5108);
             Socket socket = serverSocket.accept()) {
            BufferedReader r =
                    new BufferedReader(
                            new InputStreamReader(socket.getInputStream(),
                                    US_ASCII));
            BufferedWriter w =
                    new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream(),
                                    US_ASCII));
            Player playerProxy = new RemotePlayerProxy(socket,r,w);

            playerProxy.initPlayers(storage.id, storage.playerNames);
            playerProxy.receiveInfo(storage.receiveInfo);
            playerProxy.updateState(storage.gs, storage.ps);
            playerProxy.setInitialTicketChoice(storage.tickets);

            assertEquals(storage.tickets, playerProxy.chooseInitialTickets());
            assertEquals(storage.nextTurn, playerProxy.nextTurn());
            assertEquals(storage.tickets, playerProxy.chooseTickets(storage.tickets));
            assertEquals(storage.drawSlot, playerProxy.drawSlot());
            assertEquals(storage.route, playerProxy.claimedRoute());
            assertEquals(storage.cards, playerProxy.initialClaimCards());
            assertEquals(storage.cards, playerProxy.chooseAdditionalCards(List.of(storage.cards, SortedBag.of(Card.BLACK))));

        }
        System.out.println("Server done!");
    }
}

final class TestClient {
    public static void main(String[] args) {
        System.out.println("Starting client!");
        RemotePlayerClient playerClient = new RemotePlayerClient(
                new TestPlayer(), "localhost", 5108
        );
        playerClient.run();
        System.out.println("\nClient done!");
    }

    private final static class TestPlayer implements Player {

        @Override
        public void initPlayers(PlayerId ownId, Map<PlayerId, String> names) {
            System.out.println(MessageId.INIT_PLAYERS.name()+".........\n");
            System.out.printf("ownId: %s\n", ownId);
            System.out.printf("playerNames: %s\n", names);
            assertEquals(ownId, storage.id);
            assertEquals(names, storage.playerNames);
        }

        @Override
        public void initConstants(Map<PlayerId, String> playerColors, List<Integer> constants){

        }

        @Override
        public void receiveInfo(String info) {
            System.out.println(MessageId.RECEIVE_INFO.name()+".........\n");
            System.out.printf("info: %s\n", info);
            assertEquals(info, storage.receiveInfo);
        }

        @Override
        public void updateState(PublicGameState newState, PlayerState ownState) {
            System.out.println(MessageId.UPDATE_STATE.name()+".........\n");
            System.out.printf("newPublicGameState: %s\n", newState);
            System.out.printf("ownPlayerState: %s\n", ownState);
            storage.assertEqualsPublicGameState(newState, storage.gs);
            storage.assertEqualsPlayerState(ownState, storage.ps);
        }

        @Override
        public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
            System.out.println(MessageId.SET_INITIAL_TICKETS.name()+".........\n");
            System.out.printf("tickets: %s\n", tickets);
            assertEquals(tickets.toList(), storage.tickets.toList());
        }

        @Override
        public SortedBag<Ticket> chooseInitialTickets() {
            System.out.println(MessageId.CHOOSE_INITIAL_TICKETS.name()+".........\n");
            return storage.tickets;
        }

        @Override
        public TurnKind nextTurn() {
            System.out.println(MessageId.NEXT_TURN.name()+".........\n");
            return storage.nextTurn;
        }

        @Override
        public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
            System.out.println(MessageId.CHOOSE_TICKETS.name()+".........\n");
            System.out.printf("options: %s\n", options);
            return options;
        }

        @Override
        public int drawSlot() {
            System.out.println(MessageId.DRAW_SLOT.name()+".........\n");
            return storage.drawSlot;
        }

        @Override
        public Route claimedRoute() {
            System.out.println(MessageId.ROUTE.name()+".........\n");
            return storage.route;
        }

        @Override
        public SortedBag<Card> initialClaimCards() {
            System.out.println(MessageId.CARDS.name()+".........\n");
            return storage.cards;
        }

        @Override
        public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
            System.out.println(MessageId.CHOOSE_ADDITIONAL_CARDS.name()+".........\n");
            System.out.printf("options: %s\n", options);
            return options.get(0);
        }
    }
}
