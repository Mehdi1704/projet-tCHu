package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;

import java.net.Socket;
import java.util.List;
import java.util.Map;

public class RemotePlayerProxy implements Player {

    private final Socket socket;
    private final BufferedReader r;
    private final BufferedWriter w;

    /**
     *
     * @param socket
     * @param r
     * @param w
     */
    public RemotePlayerProxy(Socket socket, BufferedReader r, BufferedWriter w) {
        this.socket = socket;
        this.r = r;
        this.w = w;

    }

    /**
     *
     * @param ownId
     * @param playerNames
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        //TODO a revoir
        List<String> listOfArgs = List.of(
                Serdes.PLAYER_ID_SERDE.serialize(ownId),
                Serdes.LIST_STRING_SERDE.serialize(
                        List.of(playerNames.get(PlayerId.PLAYER_1),
                                playerNames.get(PlayerId.PLAYER_2))));
        sendMessage(MessageId.INIT_PLAYERS, listOfArgs);
    }

    /**
     *
     * @param info information que l'on doit passé au joueur .
     */
    @Override
    public void receiveInfo(String info) {
        sendMessage(MessageId.RECEIVE_INFO,
                List.of(Serdes.STRING_SERDE.serialize(info)));
    }

    /**
     *
     * @param newState nouvel état du jeu .
     * @param ownState état du joueur .
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        sendMessage(MessageId.UPDATE_STATE,
                List.of(Serdes.PUBLIC_GAME_STATE_SERDE.serialize(newState),
                        Serdes.PLAYER_STATE_SERDE.serialize(ownState)));
    }

    /**
     *
     * @param tickets les billets qui vont être distribués en debut de partie.
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        sendMessage(MessageId.SET_INITIAL_TICKETS,
                List.of(Serdes.SORTEDBAG_TICKETS_SERDE.serialize(tickets)));
    }

    /**
     *
     * @return
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        sendMessage(MessageId.CHOOSE_INITIAL_TICKETS, List.of());
        return Serdes.SORTEDBAG_TICKETS_SERDE.deserialize(receiveMessage());
    }

    /**
     *
     * @return
     */
    @Override
    public TurnKind nextTurn() {
        sendMessage(MessageId.NEXT_TURN, List.of());
        return Serdes.TURNKIND_SERDE.deserialize(receiveMessage());
    }

    /**
     *
     * @param options les billets qu'on propose
     * @return
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        sendMessage(MessageId.CHOOSE_TICKETS, List.of(Serdes.SORTEDBAG_TICKETS_SERDE.serialize(options)));
        return Serdes.SORTEDBAG_TICKETS_SERDE.deserialize(receiveMessage());
    }

    /**
     *
     * @return
     */
    @Override
    public int drawSlot() {
        sendMessage(MessageId.DRAW_SLOT, List.of());
        return Serdes.INTEGER_SERDE.deserialize(receiveMessage());
    }

    /**
     *
     * @return
     */
    @Override
    public Route claimedRoute() {
        sendMessage(MessageId.ROUTE, List.of());
        return Serdes.ROUTE_SERDE.deserialize(receiveMessage());
    }

    /**
     *
     * @return
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        sendMessage(MessageId.CARDS, List.of());
        return Serdes.SORTEDBAG_CARDS_SERDE.deserialize(receiveMessage());
    }

    /**
     *
     * @param options
     * @return
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        sendMessage(MessageId.CHOOSE_ADDITIONAL_CARDS, List.of(Serdes.LIST_SORTEDBAG_CARDS_SERDE.serialize(options)));
        return Serdes.SORTEDBAG_CARDS_SERDE.deserialize(receiveMessage());
    }

    /**
     *
     * @param messageId
     * @param args
     */
    private void sendMessage(MessageId messageId, List<String> args) {
        try {
            if (args.isEmpty()){
                w.write(messageId.name());
            }else{
                w.write(messageId.name() + " " + String.join(" ", args));
            }
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     *
     * @return
     */
    private String receiveMessage() {
        try {
            return r.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
