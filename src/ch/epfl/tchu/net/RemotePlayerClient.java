package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.US_ASCII;

public class RemotePlayerClient implements Player {

    Player player;
    String portName;
    int port;
    public RemotePlayerClient(Player player, String portName, int port){
        this.player = player;
        this.portName = portName;
        this.port = port;

    }

    public void run(){
        try (Socket s = new Socket("localhost", 5108);
             BufferedReader r =
                     new BufferedReader(
                             new InputStreamReader(s.getInputStream(),
                                     US_ASCII));
             BufferedWriter w =
                     new BufferedWriter(
                             new OutputStreamWriter(s.getOutputStream(),
                                     US_ASCII))) {
            int i = 2021;
            w.write(String.valueOf(i));
            w.write('\n');
            w.flush();
            //int succ = Integer.parseInt(r.readLine());
            //System.out.printf("succ(%d) = %d%n", i, succ);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
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
