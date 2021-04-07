package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
    Random rng = TestRandomizer.newRandom();
    Map<PlayerId, String> playerNames = Map.of(PlayerId.PLAYER_1, "PlayerONE", PlayerId.PLAYER_2, "PlayerTWO");

    private void print(Map<PlayerId, TestPlayer> players) {
        System.out.println("\nACTIONS");
        players.get(PlayerId.PLAYER_1).outCommands().forEach(System.out::println);
        System.out.println("\nACTIONS");
        players.get(PlayerId.PLAYER_2).outCommands().forEach(System.out::println);
        System.out.println("\nMESSAGES");
        players.get(PlayerId.PLAYER_1).outStrings().forEach(System.out::println);
    }

    private void test(Map<PlayerId, TestPlayer> players) {
        assertEquals(players.get(PlayerId.PLAYER_2).outStrings(), players.get(PlayerId.PLAYER_1).outStrings());
        assertTrue(players.get(PlayerId.PLAYER_1).outCommands().size() >= players.get(PlayerId.PLAYER_1).getTurnCounter());

        //Nbr of carts in game, =110 at all time
        assertEquals(players.get(PlayerId.PLAYER_1).cardStateSize().size(), players.get(PlayerId.PLAYER_1).ownCardsSize().size());
        assertEquals(players.get(PlayerId.PLAYER_2).cardStateSize().size(), players.get(PlayerId.PLAYER_2).ownCardsSize().size());
        assertEquals(players.get(PlayerId.PLAYER_1).cardStateSize(), players.get(PlayerId.PLAYER_2).cardStateSize());
        for (int i=0; i< players.get(PlayerId.PLAYER_1).cardStateSize().size(); i++) {
            assertEquals(110,
                    players.get(PlayerId.PLAYER_1).cardStateSize().get(i) + players.get(PlayerId.PLAYER_1).ownCardsSize().get(i)+players.get(PlayerId.PLAYER_2).ownCardsSize().get(i));
            assertEquals(110,
                    players.get(PlayerId.PLAYER_2).cardStateSize().get(i) + players.get(PlayerId.PLAYER_1).ownCardsSize().get(i)+players.get(PlayerId.PLAYER_2).ownCardsSize().get(i));
        }
    }

    @Test
    void playException1() {
        TestPlayer p1 = new TestPlayer(rng, List.copyOf(ChMap.routes()), 1);
        TestPlayer p2 = new TestPlayer(rng, List.copyOf(ChMap.routes()), 1);
        Map<PlayerId, Player> playersE = Map.of(PlayerId.PLAYER_1, p1, PlayerId.PLAYER_2, p2);
        Map<PlayerId, String> playerNamesE = Map.of(PlayerId.PLAYER_1, "PlayerONE");

        assertThrows(IllegalArgumentException.class, () ->
                Game.play(playersE, playerNamesE, SortedBag.of(ChMap.tickets()), rng));
    }

    @Test
    void playException2() {
        TestPlayer p1 = new TestPlayer(rng, List.copyOf(ChMap.routes()), 1);
        Map<PlayerId, Player> playersE = Map.of(PlayerId.PLAYER_1, p1);
        Map<PlayerId, String> playerNamesE = Map.of(PlayerId.PLAYER_1, "PlayerONE", PlayerId.PLAYER_2, "PlayerTWO");

        assertThrows(IllegalArgumentException.class, () ->
                Game.play(playersE, playerNamesE, SortedBag.of(ChMap.tickets()), rng));
    }

    @Test
    void playCheckWithRoutes() {
        List<Route> claimableRoutes = List.copyOf(ChMap.routes());
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());

        TestPlayer p1 = new TestPlayer(rng, claimableRoutes, 1);
        TestPlayer p2 = new TestPlayer(rng, claimableRoutes, 1);
        Map<PlayerId, TestPlayer> players = Map.of(PlayerId.PLAYER_1, p1, PlayerId.PLAYER_2, p2);

        Game.play(Map.copyOf(players), playerNames, tickets, rng);
        print(players);    //49, 40
        test(players);
    }

    @Test
    void playCheckWithNoRoutesNoTickets() {
        List<Route> claimableRoutes = List.of();
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().subList(0, Constants.INITIAL_TICKETS_COUNT * 2));

        TestPlayer p1 = new TestPlayer(rng, claimableRoutes, 1);
        TestPlayer p2 = new TestPlayer(rng, claimableRoutes, 1);
        Map<PlayerId, TestPlayer> players = Map.of(PlayerId.PLAYER_1, p1, PlayerId.PLAYER_2, p2);

        assertThrows(Error.class, () -> Game.play(Map.copyOf(players), playerNames, tickets, rng));
        print(players);
        test(players);
    }

    @Test
    void playCheckWithNoRoutesNoCards() {
        List<Route> claimableRoutes = List.of();
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());

        TestPlayer p1 = new TestPlayer(rng, claimableRoutes, 2);
        TestPlayer p2 = new TestPlayer(rng, claimableRoutes, 2);
        Map<PlayerId, TestPlayer> players = Map.of(PlayerId.PLAYER_1, p1, PlayerId.PLAYER_2, p2);

        assertThrows(Error.class, () -> Game.play(Map.copyOf(players), playerNames, tickets, rng));
        print(players);
        test(players);
    }

}

final class TestPlayer implements Player {
    private static final int TURN_LIMIT = 1000;

    //init
    private PlayerId ownId;
    private final Random rng;
    private final List<Route> claimableRoutes;

    //update
    private int turnCounter = 0;
    private final int mode;
    private PlayerState ownState;
    private GameState gameState;

    private Route routeToClaim;
    private SortedBag<Card> initialClaimCards;

    //Testing
    private final List<String> outStrings = new ArrayList<>();
    private final List<String> outCommands = new ArrayList<>();
    private final List<Integer> cardStateSize = new ArrayList<>();
    private final List<Integer> ownCardsSize = new ArrayList<>();

    //Storing
    private SortedBag<Ticket> tickets = SortedBag.of();

    public TestPlayer(Random rng, List<Route> claimableRoutes, int mode) {
        this.rng = rng;
        this.claimableRoutes = List.copyOf(claimableRoutes);
        this.mode = mode;
        outCommands.add("->Constructed<-");
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        this.ownId = ownId;
        outCommands.add(ownId + "(" + turnCounter + ")" + " ->initPlayers");
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        this.gameState = (GameState) newState;
        this.ownState = ownState;

        outCommands.add(ownId + "(" + turnCounter + ")" + " ->updateState");
        cardStateSize.add(gameState.cardState().totalSize());
        ownCardsSize.add(ownState.cardCount());
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        if (this.tickets.size() > 0)
            throw new Error("Already Initialised");

        this.tickets = tickets;
        outCommands.add(ownId + "(" + turnCounter + ")" + " ->setInitialTicketChoice");
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        SortedBag<Ticket> chosenTickets = SortedBag.of(tickets.toList()
                .subList(0, rng.nextInt(Constants.INITIAL_TICKETS_COUNT + 1)));

        outCommands.add(ownId + "(" + turnCounter + ")" + " ->chooseInitialTickets: "+chosenTickets);
        return chosenTickets;
    }

    @Override
    public TurnKind nextTurn() {
        if (turnCounter > TURN_LIMIT)
            throw new Error("Too many rounds being played !");

        Route route;
        turnCounter += 1;
        outCommands.add("");

        if (!claimableRoutes.isEmpty()) {
            do {
                int routeIndex = rng.nextInt(claimableRoutes.size());
                route = claimableRoutes.get(routeIndex);

                if (ownState.canClaimRoute(route)) {
                    List<SortedBag<Card>> cards = ownState.possibleClaimCards(route);
                    routeToClaim = route;
                    initialClaimCards = cards.get(0);

                    outCommands.add(ownId + "(" + turnCounter + ")" + " ->CLAIM_ROUTE: " + routeToClaim + " / " + initialClaimCards);
                    return TurnKind.CLAIM_ROUTE;
                } else if (gameState.cardState().deckSize() >= 7 && mode == 1) {

                    outCommands.add(ownId + "(" + turnCounter + ")" + " ->DRAW_CARDS");
                    return TurnKind.DRAW_CARDS;
                } else if (gameState.ticketsCount() >= Constants.IN_GAME_TICKETS_COUNT && mode == 2) {

                    outCommands.add(ownId + "(" + turnCounter + ")" + " ->DRAW_TICKETS");
                    return TurnKind.DRAW_TICKETS;
                }
            } while (!ownState.canClaimRoute(route));
        } else if (gameState.cardState().deckSize() >= 7 && mode == 1) {
            outCommands.add(ownId + "(" + turnCounter + ")" + " ->DRAW_CARDS");
            return TurnKind.DRAW_CARDS;
        } else if (gameState.ticketsCount() >= Constants.IN_GAME_TICKETS_COUNT && mode == 2) {
            outCommands.add(ownId + "(" + turnCounter + ")" + " ->DRAW_TICKETS");
            return TurnKind.DRAW_TICKETS;
        }
        throw new Error("Out of moves");
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        SortedBag<Ticket> chosen = SortedBag.of(options.toList().subList(0, rng.nextInt(Constants.IN_GAME_TICKETS_COUNT + 1)));
        outCommands.add(ownId + "(" + turnCounter + ")" + " ->chosenTickets: " + chosen);
        return chosen;
    }

    @Override
    public int drawSlot() {
        int chosen = rng.nextInt(Constants.FACE_UP_CARDS_COUNT + 1) - 1;
        outCommands.add(ownId + "(" + turnCounter + ")" + " ->drawSlot: " + chosen);
        return chosen;
    }

    @Override
    public void receiveInfo(String info) {
        outCommands.add(ownId + "(" + turnCounter + ")" + " ->receiveInf");
        outStrings.add(info);
    }

    @Override
    public Route claimedRoute() {
        outCommands.add(ownId + "(" + turnCounter + ")" + " ->claimedRoute: " + routeToClaim);
        return routeToClaim;
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        outCommands.add(ownId + "(" + turnCounter + ")" + " ->initialClaimCards: " + initialClaimCards);
        return initialClaimCards;
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        outCommands.add(ownId + "(" + turnCounter + ")" + " ->chooseAdditionalCards");
        return options.isEmpty() ? null : options.get(0);
    }

    //Debugging
    public List<String> outCommands() { return List.copyOf(outCommands); }
    public List<String> outStrings() { return List.copyOf(outStrings); }
    public List<Integer> cardStateSize() { return cardStateSize; }
    public List<Integer> ownCardsSize() { return ownCardsSize; }

    public int getTurnCounter() { return turnCounter; }
}

