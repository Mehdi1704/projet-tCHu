package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.US_ASCII;

public class RemotePlayerProxy implements Player {

    private final Socket socket;
    private final BufferedReader r;
    private final BufferedWriter w;


    public RemotePlayerProxy(Socket socket, BufferedReader r, BufferedWriter w) {
        this.socket = socket;
        this.r = r;
        this.w = w;

        try (socket; r; w) {

            int i = Integer.parseInt(r.readLine());
            int i1 = i + 1;
            w.write(String.valueOf(i1));
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        //TODO a revoir
        String arg1 = Serdes.PLAYER_ID_SERDE.serialize(ownId);
        List<String> listOfSerdes = List.of(
                Serdes.STRING_SERDE.serialize(playerNames.get(PlayerId.PLAYER_1)),
                Serdes.STRING_SERDE.serialize(playerNames.get(PlayerId.PLAYER_2)));
        String arg2 = Serdes.LIST_STRING_SERDE.serialize(listOfSerdes);
        List<String> listOfArgs = List.of(arg1, arg2);

        sendMessage(MessageId.INIT_PLAYERS, listOfArgs);

    }

    @Override
    public void receiveInfo(String info) {
        sendMessage(MessageId.RECEIVE_INFO,
                List.of(Serdes.STRING_SERDE.serialize(info)));
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        sendMessage(MessageId.UPDATE_STATE,
                List.of(Serdes.PUBLIC_GAME_STATE_SERDE.serialize(newState),
                        Serdes.PLAYER_STATE_SERDE.serialize(ownState)));
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        sendMessage(MessageId.SET_INITIAL_TICKETS,
                List.of(Serdes.SORTEDBAG_TICKETS_SERDE.serialize(tickets)));
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        sendMessage(MessageId.CHOOSE_INITIAL_TICKETS, List.of());
        return Serdes.SORTEDBAG_TICKETS_SERDE.deserialize(receiveMessage(r));
    }

    @Override
    public TurnKind nextTurn() {
        sendMessage(MessageId.NEXT_TURN, List.of());
        return Serdes.TURNKIND_SERDE.deserialize(receiveMessage(r));
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        sendMessage(MessageId.CHOOSE_TICKETS, List.of(Serdes.SORTEDBAG_TICKETS_SERDE.serialize(options)));
        return Serdes.SORTEDBAG_TICKETS_SERDE.deserialize(receiveMessage(r));
    }

    @Override
    public int drawSlot() {
        sendMessage(MessageId.DRAW_SLOT, List.of());
        return Serdes.INTEGER_SERDE.deserialize(receiveMessage(r));
    }

    @Override
    public Route claimedRoute() {
        sendMessage(MessageId.ROUTE, List.of());
        return Serdes.ROUTE_SERDE.deserialize(receiveMessage(r));
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        sendMessage(MessageId.CARDS, List.of());
        return Serdes.SORTEDBAG_CARDS_SERDE.deserialize(receiveMessage(r));
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        sendMessage(MessageId.CHOOSE_ADDITIONAL_CARDS, List.of(Serdes.LIST_SORTEDBAG_CARDS_SERDE.serialize(options)));
        return Serdes.SORTEDBAG_CARDS_SERDE.deserialize(receiveMessage(r));
    }


    private void sendMessage(MessageId messageId, List<String> args) {
        try {
            //TODO pas d'espace supplementaire lorsque pas d'arguments
            String message = messageId.name() + " " + String.join(" ", args);
            w.write(message);
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String receiveMessage(BufferedReader r) {
        try {
            return r.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }
}
