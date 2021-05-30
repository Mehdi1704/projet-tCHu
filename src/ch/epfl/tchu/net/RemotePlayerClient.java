package ch.epfl.tchu.net;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.gui.MenuMain;
import javafx.scene.paint.Color;

import java.io.*;
import java.net.Socket;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * Client de joueur
 *
 * @author Mehdi Bouchoucha (314843)
 * @author Ali Ridha Mrad (314529)
 */
public final class RemotePlayerClient {

    private final Player player;
    private final String portName;
    private final int port;


    /**
     * Constructeur d'un RemotePlayerProxy
     *
     * @param player   Joueur
     * @param portName Nom du port de communication
     * @param port     Numero du port de communication
     */
    public RemotePlayerClient(Player player, String portName, int port) {
        this.player = player;
        this.portName = portName;
        this.port = port;
    }

    /**
     * Methode servant à lire les messages afin de les traiter en fonction des cas
     */
    public void run() {
        try (Socket s = new Socket(portName, port);
             BufferedReader r = new BufferedReader(
                     new InputStreamReader(s.getInputStream(), US_ASCII));
             BufferedWriter w = new BufferedWriter(
                     new OutputStreamWriter(s.getOutputStream(), US_ASCII))) {
            String[] message;

            String readLine = r.readLine();
            while (readLine != null) {
                message = readLine.split(Pattern.quote(" "), -1);
                checkMessage(message, w);
                readLine = r.readLine();
            }

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

    /**
     * Methode qui détermine la méthode à utiliser, déserialise ses arguments et renvoie un résultat si nécessaire
     *
     * @param message Message recu par la prise, composé du type de la méthode et ses arguments
     * @param w       Objet qui écrit les messages
     * @throws IOException Si la connexion entre le client et le serveur est interrompue
     */
    private void checkMessage(String[] message, BufferedWriter w) throws IOException {
        switch (MessageId.valueOf(message[0])) {
            case INIT_PLAYERS:
                Map<PlayerId, String> playerNames = new EnumMap<>(PlayerId.class);
                playerNames.put(PlayerId.PLAYER_1, Serdes.LIST_STRING_SERDE.deserialize(message[2]).get(0));
                playerNames.put(PlayerId.PLAYER_2, Serdes.LIST_STRING_SERDE.deserialize(message[2]).get(1));
                player.initPlayers(Serdes.PLAYER_ID_SERDE.deserialize(message[1]), playerNames);
                break;
            case INIT_CONSTANTS:
                MenuMain.setColors(List.of(
                        Serdes.STRING_SERDE.deserialize(message[1]),
                        Serdes.STRING_SERDE.deserialize(message[2])));
                MenuMain.setConstants(List.of(message[3],message[4],message[5]));
                break;
            case RECEIVE_INFO:
                player.receiveInfo(Serdes.STRING_SERDE.deserialize(message[1]));
                break;
            case UPDATE_STATE:
                player.updateState(
                        Serdes.PUBLIC_GAME_STATE_SERDE.deserialize(message[1]),
                        Serdes.PLAYER_STATE_SERDE.deserialize(message[2]));
                break;
            case SET_INITIAL_TICKETS:
                player.setInitialTicketChoice(
                        Serdes.SORTEDBAG_TICKETS_SERDE.deserialize(message[1]));
                break;
            case CHOOSE_INITIAL_TICKETS:
                sendOutput(w, Serdes.SORTEDBAG_TICKETS_SERDE.serialize(player.chooseInitialTickets()));
                break;
            case NEXT_TURN:
                sendOutput(w, Serdes.TURNKIND_SERDE.serialize(player.nextTurn()));
                break;
            case CHOOSE_TICKETS:
                sendOutput(w, Serdes.SORTEDBAG_TICKETS_SERDE.serialize(
                        player.chooseTickets(
                                Serdes.SORTEDBAG_TICKETS_SERDE.deserialize(message[1]))));
                break;
            case DRAW_SLOT:
                sendOutput(w, Serdes.INTEGER_SERDE.serialize(player.drawSlot()));
                break;
            case ROUTE:
                sendOutput(w, Serdes.ROUTE_SERDE.serialize(player.claimedRoute()));
                break;
            case CARDS:
                sendOutput(w, Serdes.SORTEDBAG_CARDS_SERDE.serialize(player.initialClaimCards()));
                break;
            case CHOOSE_ADDITIONAL_CARDS:
                sendOutput(w, Serdes.SORTEDBAG_CARDS_SERDE.serialize(
                        player.chooseAdditionalCards(
                                Serdes.LIST_SORTEDBAG_CARDS_SERDE.deserialize(message[1]))));
                break;
        }
    }

    /**
     * @param w      Objet qui écrit les messages
     * @param output le résultat (sérialisé) de la méthode à renvoyer sur la prise
     * @throws IOException Si la connexion entre le client et le serveur est interrompue
     */
    private void sendOutput(BufferedWriter w, String output) throws IOException {
        w.write(Objects.requireNonNull(output));
        w.write('\n');
        w.flush();
    }

}
