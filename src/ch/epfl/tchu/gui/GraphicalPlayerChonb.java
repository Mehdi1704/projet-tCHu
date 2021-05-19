package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static javafx.application.Platform.runLater;

public class GraphicalPlayerChonb implements Player {

    private GraphicalPlayer graphicalPlayer;
    private final BlockingQueue<ActionHandlers.ChooseTicketsHandler> ticketsHandlerBQ = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<SortedBag<Ticket>> ticketsBQ = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<TurnKind> turnKindBQ = new ArrayBlockingQueue<>(1);

    public GraphicalPlayerChonb(){
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

        runLater(() -> {graphicalPlayer.chooseTickets(tickets, tickets1 -> {
            try{
                    ticketsBQ.put(tickets1);
            }catch (InterruptedException e){
                throw new Error();
            }
        });
    });
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        SortedBag<Ticket> tickets;
        try{
            tickets = ticketsBQ.take();
        }catch (InterruptedException e){
            throw new Error();
        }
        return tickets;
    }

    @Override
    public TurnKind nextTurn() {/*
        runLater(() -> {
            try {
                graphicalPlayer.startTurn();
                return turnKindBQ.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }*/ return null;}

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        runLater(() -> {
            try {
                graphicalPlayer.chooseTickets(options, ticketsHandlerBQ.put());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
       return SortedBag.of() ;
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
        return null;
    }
}
