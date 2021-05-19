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
    private final BlockingQueue<SortedBag<Card>> cardsBQ = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<TurnKind> turnKindBQ = new ArrayBlockingQueue<>(1);

    public GraphicalPlayerAdapter(){
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
       runLater(()-> graphicalPlayer = new GraphicalPlayer(ownId, playerNames));

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
        BlockingQueue<ActionHandlers.DrawTicketsHandler> ticketsHandlerBQ = new ArrayBlockingQueue<>(1);
        BlockingQueue<ActionHandlers.ClaimRouteHandler> routesHandlerBQ = new ArrayBlockingQueue<>(1);
        BlockingQueue<ActionHandlers.DrawCardHandler> cardHandlerBQ = new ArrayBlockingQueue<>(1);

        runLater(() -> {
            graphicalPlayer.startTurn(takeBlockingQueue(ticketsHandlerBQ),
                                      takeBlockingQueue(routesHandlerBQ),
                                      takeBlockingQueue(cardHandlerBQ));
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
        //Une boucle if vérifie (grâce à la méthode peek()) si la queue qui gère les face up cartes est différent de null (donc non vide)
        //Si c’est le cas (donc la file contient quelque chose) alors on fait take() sur cette BlockingQueue
        //Sinon, donc si la queue est vide, alors
        //1) on crée un DrawCardHandler qui met le slot donné dans la queue,
        //2) on appelle la méthode drawCard du graphicalPlayer (avec un runLater) et
        //3) on fait take() sur cette BlockingQueue
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
        runLater(() -> graphicalPlayer.chooseAdditionalCards(options, c -> putBlockingQueue(cardsBQ, c)));
        return takeBlockingQueue(cardsBQ);
    }

    private <T> void putBlockingQueue(BlockingQueue<T> queue, T object){
        try {
            queue.put(object);
        } catch (InterruptedException e) {
            throw new Error();
        }
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
