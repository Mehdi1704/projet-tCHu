package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.List;
import java.util.Map;

public class RemotePlayerClientTest {

    public static final class TestClient {

        public static void main(String[] args) {
            System.out.println("Starting client!");
            RemotePlayerClient playerClient =
                    new RemotePlayerClient(new TestPlayer(),
                            "localhost",
                            5108);
            playerClient.run();
            System.out.println("Client done!");
        }

        private final static class TestPlayer implements Player {
            @Override
            public void initPlayers(PlayerId ownId,
                                    Map<PlayerId, String> names) {
                System.out.println(MessageId.INIT_PLAYERS.name()+".........\n");
                System.out.printf("ownId: %s\n", ownId);
                System.out.printf("playerNames: %s\n", names);
            }

            @Override
            public void initConstants(Map<PlayerId, String> playerColors, List<Integer> constants){

            }

            @Override
            public void receiveInfo(String info) {
                System.out.println(MessageId.RECEIVE_INFO.name()+".........\n");
                System.out.printf("info: %s\n", info);
            }

            @Override
            public void updateState(PublicGameState newState, PlayerState ownState) {
                System.out.println(MessageId.UPDATE_STATE.name()+".........\n");
                System.out.printf("newPublicGameState: %s\n", newState);
                System.out.printf("ownPlayerState: %s\n", ownState);
            }

            @Override
            public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
                System.out.println(MessageId.SET_INITIAL_TICKETS.name()+".........\n");

                System.out.printf("tickets: %s\n", tickets);
            }

            @Override
            public SortedBag<Ticket> chooseInitialTickets() {
                return null;
            }

            @Override
            public TurnKind nextTurn() {
                return null;
            }

            @Override
            public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
                System.out.printf("options: %s\n", options);
                return null;
            }

            @Override
            public int drawSlot() {
                return 0;
            }

            @Override
            public Route claimedRoute() {
                return null;
            }

            @Override
            public SortedBag<Card> initialClaimCards() {
                return null;
            }

            @Override
            public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
                System.out.printf("options: %s\n", options);
                return null;
            }

            // … autres méthodes de Player
        }
    }
}
