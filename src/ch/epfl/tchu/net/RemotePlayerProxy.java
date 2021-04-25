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

    public final Socket socket;

    public RemotePlayerProxy(Socket socket) {
        this.socket = socket;

        try (ServerSocket s0 = new ServerSocket(5108);
             Socket s = s0.accept();
             BufferedReader r =
                     new BufferedReader(
                             new InputStreamReader(s.getInputStream(),
                                     US_ASCII));
             BufferedWriter w =
                     new BufferedWriter(
                             new OutputStreamWriter(s.getOutputStream(),
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

    //TODO cette methode peut servir quelque part
    // Je suis pas sur si elle devrait etre ici
    private void checkMessage(MessageId messageId, Serde serde, BufferedReader r){
        switch(messageId){
            case INIT_PLAYERS:
                initPlayers();
            case RECEIVE_INFO:
                receiveInfo();
            case UPDATE_STATE:
                updateState();
            case SET_INITIAL_TICKETS:
                setInitialTicketChoice();
            case CHOOSE_INITIAL_TICKETS:
                chooseInitialTickets();
            case NEXT_TURN:
                nextTurn();
            case CHOOSE_TICKETS:
                chooseTickets();
            case DRAW_SLOT:
                drawSlot();
            case ROUTE:
                claimedRoute();
            case CARDS:
                initialClaimCards();
            case CHOOSE_ADDITIONAL_CARDS:
                chooseAdditionalCards();
                break;
        }
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {


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
}
