package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;

import java.net.Socket;
import java.util.List;
import java.util.Map;

/**
 * Proxy de joueur
 *
 * @author Mehdi Bouchoucha (314843)
 * @author Ali Ridha Mrad (314529)
 */
public final class RemotePlayerProxy implements Player {

    private final Socket socket;
    private final BufferedReader r;
    private final BufferedWriter w;

    /**
     * Constructeur d'un RemotePlayerProxy
     *
     * @param socket Prise dans laquelle les messages vont s'échanger
     * @param r      Objet qui lit les messages
     * @param w      Objet qui écrit les messages
     */
    public RemotePlayerProxy(Socket socket, BufferedReader r, BufferedWriter w) {
        this.socket = socket;
        this.r = r;
        this.w = w;
    }

    /**
     * Envoie sur la prise le MessageId de la methode et ses arguments listés et sérialisés
     *
     * @param ownId       Identité du joueur appelant la methode
     * @param playerNames Noms des différents joueurs se trouvant dans la Map
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        List<String> listOfArgs = List.of(
                Serdes.PLAYER_ID_SERDE.serialize(ownId),
                Serdes.LIST_STRING_SERDE.serialize(
                        List.of(playerNames.get(PlayerId.PLAYER_1),
                                playerNames.get(PlayerId.PLAYER_2))));
        sendMessage(MessageId.INIT_PLAYERS, listOfArgs);
    }

    @Override
    public void initConstants(Map<PlayerId, String> playerColors, List<Integer> constants){
        System.out.println("INIT-COLORS: "+playerColors);
        System.out.println("INIT-CONSTANTS: "+constants);
        List<String> listOfArgs = List.of(
                Serdes.STRING_SERDE.serialize(playerColors.get(PlayerId.PLAYER_1)),
                Serdes.STRING_SERDE.serialize(playerColors.get(PlayerId.PLAYER_2)),
                Serdes.INTEGER_SERDE.serialize(constants.get(0)),
                Serdes.INTEGER_SERDE.serialize(constants.get(1)),
                Serdes.INTEGER_SERDE.serialize(constants.get(2)));
        sendMessage(MessageId.INIT_CONSTANTS, listOfArgs);
    }

    /**
     * Envoie sur la prise le MessageId de la methode et ses arguments listés et sérialisés
     *
     * @param info information que l'on doit passé au joueur .
     */
    @Override
    public void receiveInfo(String info) {
        sendMessage(MessageId.RECEIVE_INFO,
                List.of(Serdes.STRING_SERDE.serialize(info)));
    }

    /**
     * Envoie sur la prise le MessageId de la methode et ses arguments listés et sérialisés
     *
     * @param newState nouvel état du jeu
     * @param ownState état du joueur
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        sendMessage(MessageId.UPDATE_STATE,
                List.of(Serdes.PUBLIC_GAME_STATE_SERDE.serialize(newState),
                        Serdes.PLAYER_STATE_SERDE.serialize(ownState)));
    }

    /**
     * Envoie sur la prise le MessageId de la methode et ses arguments listés et sérialisés
     *
     * @param tickets les billets qui vont être distribués en debut de partie
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        sendMessage(MessageId.SET_INITIAL_TICKETS,
                List.of(Serdes.SORTEDBAG_TICKETS_SERDE.serialize(tickets)));
    }

    /**
     * Envoie sur la prise le MessageId de la methode et ses arguments listés et sérialisés
     *
     * @return La réponse déserialisée qu'elle recoit par la prise suite à son envoi
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        sendMessage(MessageId.CHOOSE_INITIAL_TICKETS, List.of());
        return Serdes.SORTEDBAG_TICKETS_SERDE.deserialize(receiveMessage());
    }

    /**
     * Envoie sur la prise le MessageId de la methode et ses arguments listés et sérialisés
     *
     * @return La réponse déserialisée qu'elle recoit par la prise suite à son envoi
     */
    @Override
    public TurnKind nextTurn() {
        sendMessage(MessageId.NEXT_TURN, List.of());
        return Serdes.TURNKIND_SERDE.deserialize(receiveMessage());
    }

    /**
     * Envoie sur la prise le MessageId de la methode et ses arguments listés et sérialisés
     *
     * @param options les billets qu'on propose
     * @return La réponse déserialisée qu'elle recoit par la prise suite à son envoi
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        sendMessage(MessageId.CHOOSE_TICKETS, List.of(Serdes.SORTEDBAG_TICKETS_SERDE.serialize(options)));
        return Serdes.SORTEDBAG_TICKETS_SERDE.deserialize(receiveMessage());
    }

    /**
     * Envoie sur la prise le MessageId de la methode et ses arguments listés et sérialisés
     *
     * @return La réponse déserialisée qu'elle recoit par la prise suite à son envoi
     */
    @Override
    public int drawSlot() {
        sendMessage(MessageId.DRAW_SLOT, List.of());
        return Serdes.INTEGER_SERDE.deserialize(receiveMessage());
    }

    /**
     * Envoie sur la prise le MessageId de la methode et ses arguments listés et sérialisés
     *
     * @return La réponse déserialisée qu'elle recoit par la prise suite à son envoi
     */
    @Override
    public Route claimedRoute() {
        sendMessage(MessageId.ROUTE, List.of());
        return Serdes.ROUTE_SERDE.deserialize(receiveMessage());
    }

    /**
     * Envoie sur la prise le MessageId de la methode et ses arguments listés et sérialisés
     *
     * @return La réponse déserialisée qu'elle recoit par la prise suite à son envoi
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        sendMessage(MessageId.CARDS, List.of());
        return Serdes.SORTEDBAG_CARDS_SERDE.deserialize(receiveMessage());
    }

    /**
     * Envoie sur la prise le MessageId de la methode et ses arguments listés et sérialisés
     *
     * @param options Cartes que le joueur peut jouer
     * @return La réponse déserialisée qu'elle recoit par la prise suite à son envoi
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        sendMessage(MessageId.CHOOSE_ADDITIONAL_CARDS, List.of(Serdes.LIST_SORTEDBAG_CARDS_SERDE.serialize(options)));
        return Serdes.SORTEDBAG_CARDS_SERDE.deserialize(receiveMessage());
    }

    /**
     * Methode auxiliaire servant à envoyer un message sur la prise
     *
     * @param messageId Message correspondant à la méthode appelée
     * @param args      Liste d'arguments de la méthode, sérialisés et listés dans l'ordre
     */
    private void sendMessage(MessageId messageId, List<String> args) {
        try {
            if (args.isEmpty()) {
                w.write(messageId.name());
            } else {
                String spaceSeparator = " ";
                w.write(messageId.name() + spaceSeparator + String.join(spaceSeparator, args));
            }
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Methode auxiliaire servant à réceptionner un message de la prise
     *
     * @return Le message recu sous forme de String
     */
    private String receiveMessage() {
        try {
            return r.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
