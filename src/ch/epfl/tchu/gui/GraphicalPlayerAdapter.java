package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static javafx.application.Platform.runLater;

public class GraphicalPlayerAdapter implements Player {

    private GraphicalPlayer graphicalPlayer;
    private final BlockingQueue<SortedBag<Ticket>> ticketsBQ = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<Route> routesBQ = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<SortedBag<Card>> cardsBQ = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<Integer> slotBQ = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<TurnKind> turnKindBQ = new ArrayBlockingQueue<>(1);

    public GraphicalPlayerAdapter(){
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        BlockingQueue<GraphicalPlayer> playerQueue = new ArrayBlockingQueue<>(1);
        runLater(()-> playerQueue.add(new GraphicalPlayer(ownId, playerNames)));
        graphicalPlayer = takeBlockingQueue(playerQueue);
    }

    @Override
    public void receiveInfo(String info) {
        runLater(() -> graphicalPlayer.receiveInfo(info));
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        runLater(() -> graphicalPlayer.setState(newState, ownState));
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        runLater(() -> graphicalPlayer.chooseTickets(
                            tickets, t -> putBlockingQueue(ticketsBQ, t)));
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        return takeBlockingQueue(ticketsBQ);
    }

    @Override
    public TurnKind nextTurn() {
        ActionHandlers.DrawTicketsHandler ticketsHandler;
        ActionHandlers.ClaimRouteHandler routesHandler;
        ActionHandlers.DrawCardHandler cardHandler;

        ticketsHandler = () -> putBlockingQueue(turnKindBQ, TurnKind.DRAW_TICKETS);
        routesHandler = (route, card) -> {
            putBlockingQueue(turnKindBQ, TurnKind.CLAIM_ROUTE);
            putBlockingQueue(routesBQ, route);
            putBlockingQueue(cardsBQ, card);
        };
        cardHandler = (slot) -> {
            putBlockingQueue(turnKindBQ, TurnKind.DRAW_CARDS);
            putBlockingQueue(slotBQ, slot);

        };

        runLater(() -> {
            graphicalPlayer.startTurn(ticketsHandler,
                                      routesHandler,
                                      cardHandler);
        });
        return takeBlockingQueue(turnKindBQ);
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        runLater(() -> graphicalPlayer.chooseTickets(options, t -> putBlockingQueue(ticketsBQ, t)));
        return takeBlockingQueue(ticketsBQ);
    }

    @Override
    public int drawSlot() {
        if (slotBQ.size()!= 1 ){
            ActionHandlers.DrawCardHandler cardHandler;
            cardHandler = (slot) -> putBlockingQueue(slotBQ, slot);
            runLater(() -> graphicalPlayer.drawCard(cardHandler));
        }
        return takeBlockingQueue(slotBQ);
    }

    @Override
    public Route claimedRoute() {
        return takeBlockingQueue(routesBQ);
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        return takeBlockingQueue(cardsBQ);
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        runLater(() -> graphicalPlayer.chooseAdditionalCards(options, c -> putBlockingQueue(cardsBQ, c)));
        return takeBlockingQueue(cardsBQ);
    }

    private <T> void putBlockingQueue(BlockingQueue<T> queue, T object){
        queue.add(object);
    }

    private <T> T takeBlockingQueue(BlockingQueue<T> queue){
        T object;
        try {
            object = queue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
        return object;
    }
}
