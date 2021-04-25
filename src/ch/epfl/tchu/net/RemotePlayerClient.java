package ch.epfl.tchu.net;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.US_ASCII;

public class RemotePlayerClient{

    private final Player player;
    private final String portName;
    private final int port;


    /**
     * Constructeur
     *
     * @param player
     * @param portName
     * @param port
     */
    public RemotePlayerClient(Player player, String portName, int port){
        this.player = player;
        this.portName = portName;
        this.port = port;
    }

    //cette exception est levée en cas d’erreur d’entrée/sortie.
    // Dans le projet, cette erreur pourra p.ex. être levée si la connexion entre
    // le client et le serveur est interrompue, p.ex. suite à la perte de la connexion Internet (problème de wifi, p.ex.)

    /**
     *
     */
    public void run(){
        try (Socket s = new Socket(portName, port);
             BufferedReader r = new BufferedReader(
                                        new InputStreamReader(s.getInputStream(), US_ASCII));
             BufferedWriter w = new BufferedWriter(
                                        new OutputStreamWriter(s.getOutputStream(), US_ASCII))) {
            String[] message;

            do { //TODO pas encore stable
                String readLine = r.readLine();
                message = readLine.split(Pattern.quote(" "), -1);
                checkMessage(message, w);
            } while (r.readLine()!=null);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
            }

    }

    /**
     *
     * @param message
     * @param w
     * @throws IOException
     */
    private void checkMessage(String[] message, BufferedWriter w) throws IOException {
        switch(MessageId.valueOf(message[0])){
            case INIT_PLAYERS:
                Map<PlayerId, String> playerNames = new EnumMap<>(PlayerId.class);
                playerNames.put(PlayerId.PLAYER_1, Serdes.LIST_STRING_SERDE.deserialize(message[2]).get(0));
                playerNames.put(PlayerId.PLAYER_2, Serdes.LIST_STRING_SERDE.deserialize(message[2]).get(1));
                player.initPlayers(Serdes.PLAYER_ID_SERDE.deserialize(message[1]),playerNames);
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
                sendOutput(w,Serdes.SORTEDBAG_TICKETS_SERDE.serialize(player.chooseInitialTickets()));
                break;
            case NEXT_TURN:
                sendOutput(w,Serdes.TURNKIND_SERDE.serialize(player.nextTurn()));
                break;
            case CHOOSE_TICKETS:
                sendOutput(w,Serdes.SORTEDBAG_TICKETS_SERDE.serialize(
                            player.chooseTickets(
                                    Serdes.SORTEDBAG_TICKETS_SERDE.deserialize(message[1]))));
                break;
            case DRAW_SLOT:
                sendOutput(w,Serdes.INTEGER_SERDE.serialize(player.drawSlot()));
                break;
            case ROUTE:
                sendOutput(w,Serdes.ROUTE_SERDE.serialize(player.claimedRoute()));
                break;
            case CARDS:
                sendOutput(w,Serdes.SORTEDBAG_CARDS_SERDE.serialize(player.initialClaimCards()));
                break;
            case CHOOSE_ADDITIONAL_CARDS:
                sendOutput(w,Serdes.SORTEDBAG_CARDS_SERDE.serialize(
                        player.chooseAdditionalCards(
                                Serdes.LIST_SORTEDBAG_CARDS_SERDE.deserialize(message[1]))));
                break;
        }
    }

    /**
     *
     * @param w
     * @param output
     * @throws IOException
     */
    private void sendOutput(BufferedWriter w, String output) throws IOException {
        Preconditions.checkIfEmptyString(output);
        w.write(Objects.requireNonNull(output));
        w.write('\n');
        w.flush();
    }

}
