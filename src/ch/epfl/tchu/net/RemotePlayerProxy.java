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

    public RemotePlayerProxy(Socket socket) {
        this.socket = socket;

        try (socket;
             BufferedReader r =
                     new BufferedReader(
                             new InputStreamReader(socket.getInputStream(),
                                     US_ASCII));
             BufferedWriter w =
                     new BufferedWriter(
                             new OutputStreamWriter(socket.getOutputStream(),
                                     US_ASCII))) {
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

        String arg1 = Serdes.PLAYER_ID_SERDE.serialize(ownId);
        List<String> listOfSerdes = List.of(
                Serdes.STRING_SERDE.serialize(playerNames.get(PlayerId.PLAYER_1)),
                Serdes.STRING_SERDE.serialize(playerNames.get(PlayerId.PLAYER_2)));
        String arg2 = Serdes.LIST_STRING_SERDE.serialize(listOfSerdes);
        List<String> listOfArgs = List.of(arg1,arg2);

        sendMessage(MessageId.INIT_PLAYERS,listOfArgs);

    }

    @Override
    public void receiveInfo(String info) {

    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {

    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {

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
        return null;
    }


    private void sendMessage(MessageId messageId, List<String> args) {
        String message = messageId.name() + " " + String.join(" ", args);
        // TODO: there is a superfluous space when the args array is empty
        // …suite de la méthode
    }
}
