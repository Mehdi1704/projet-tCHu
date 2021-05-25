package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static javafx.application.Platform.runLater;

/**
 *
 */
public class GraphicalPlayerAdapter implements Player {

    private GraphicalPlayer graphicalPlayer;
    private final BlockingQueue<SortedBag<Ticket>> ticketsBQ = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<Route> routesBQ = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<SortedBag<Card>> cardsBQ = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<Integer> slotBQ = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<TurnKind> turnKindBQ = new ArrayBlockingQueue<>(1);

    /**
     *
     */
    public GraphicalPlayerAdapter(){
    }

    /**
     *
     * @param ownId
     * @param playerNames
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        BlockingQueue<GraphicalPlayer> playerQueue = new ArrayBlockingQueue<>(1);
        runLater(()-> playerQueue.add(new GraphicalPlayer(ownId, playerNames)));
        graphicalPlayer = takeBlockingQueue(playerQueue);
    }

    /**
     *
     * @param info information que l'on doit passé au joueur .
     */
    @Override
    public void receiveInfo(String info) {
        runLater(() -> graphicalPlayer.receiveInfo(info));
    }

    /**
     *
     * @param newState nouvel état du jeu .
     * @param ownState état du joueur .
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        runLater(() -> graphicalPlayer.setState(newState, ownState));
    }

    /**
     *
     * @param tickets les billets qui vont être distribués en debut de partie.
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        runLater(() -> graphicalPlayer.chooseTickets(
                            tickets, t -> putBlockingQueue(ticketsBQ, t)));
    }

    /**
     *
     * @return
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        return takeBlockingQueue(ticketsBQ);
    }

    /**
     *
     * @return
     */
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

        runLater(() -> graphicalPlayer.startTurn(ticketsHandler,
                                      routesHandler,
                                      cardHandler));

        return takeBlockingQueue(turnKindBQ);
    }

    /**
     *
     * @param options les billets qu'on propose
     * @return
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        runLater(() -> graphicalPlayer.chooseTickets(options, t -> putBlockingQueue(ticketsBQ, t)));
        return takeBlockingQueue(ticketsBQ);
    }

    /**
     *
     * @return
     */
    @Override
    public int drawSlot() {
        if (slotBQ.size()!= 1 ){
            ActionHandlers.DrawCardHandler cardHandler;
            cardHandler = (slot) -> putBlockingQueue(slotBQ, slot);
            runLater(() -> graphicalPlayer.drawCard(cardHandler));
        }
        return takeBlockingQueue(slotBQ);
    }

    /**
     *
     * @return
     */
    @Override
    public Route claimedRoute() {
        return takeBlockingQueue(routesBQ);
    }

    /**
     *
     * @return
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        return takeBlockingQueue(cardsBQ);
    }

    /**
     *
     * @param options
     * @return
     */
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
