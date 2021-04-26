package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.gui.Info;
import ch.epfl.test.TestRandomizer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;
import static java.nio.charset.StandardCharsets.US_ASCII;

public class RemotePlayerProxyTest {

    public static void main(String[] args) throws IOException {
        System.out.println("Starting server!");
        try (ServerSocket serverSocket = new ServerSocket(5108);
             Socket socket = serverSocket.accept()) {
            BufferedReader r =
                    new BufferedReader(
                            new InputStreamReader(socket.getInputStream(),
                                    US_ASCII));
            BufferedWriter w =
                    new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream(),
                                    US_ASCII));
            Player playerProxy = new RemotePlayerProxy(socket,r,w);

            var playerNames = Map.of(PLAYER_1, "Ada",
                    PLAYER_2, "Charles");
            playerProxy.initPlayers(PLAYER_1, playerNames);

            Map<PlayerId, Info> playerInformation = new EnumMap<>(PlayerId.class);
            playerNames.forEach((player, info) -> playerInformation.put(player, new Info(info)));
            playerProxy.receiveInfo(playerInformation.get(PLAYER_1).willPlayFirst());

            var faceUpCards = SortedBag.of(5, Card.LOCOMOTIVE).toList();
            var cardState = new PublicCardState(faceUpCards, 0, 0);
            var publicPlayerState = (PublicPlayerState) PlayerState.initial(SortedBag.of(4, Card.RED));
            var playerState = PlayerState.initial(SortedBag.of(4, Card.RED));
            var playerStateMap = Map.of(
                    PLAYER_1, publicPlayerState,
                    PLAYER_2, publicPlayerState);
            var pgs = new PublicGameState(3, cardState, PLAYER_1, playerStateMap, PLAYER_1);
            playerProxy.updateState(pgs,playerState);

            playerProxy.setInitialTicketChoice(SortedBag.of(new Ticket(ChMap.BAL, ChMap.BER, 5)));
        }
        System.out.println("Server done!");
    }


    private static final class ChMap {
        private ChMap() {
        }

        // Stations - cities
        private static final Station BAD = new Station(0, "Baden");
        private static final Station BAL = new Station(1, "Bâle");
        private static final Station BER = new Station(3, "Berne");
        private static final Station BRI = new Station(4, "Brigue");
        private static final Station BRU = new Station(5, "Brusio");
        private static final Station COI = new Station(6, "Coire");
        private static final Station DAV = new Station(7, "Davos");
        private static final Station DEL = new Station(8, "Delémont");
        private static final Station FRI = new Station(9, "Fribourg");
        private static final Station GEN = new Station(10, "Genève");
        private static final Station INT = new Station(11, "Interlaken");
        private static final Station KRE = new Station(12, "Kreuzlingen");
        private static final Station LOC = new Station(15, "Locarno");
        private static final Station LUC = new Station(16, "Lucerne");
        private static final Station LUG = new Station(17, "Lugano");
        private static final Station OLT = new Station(20, "Olten");
        private static final Station SAR = new Station(22, "Sargans");
        private static final Station SCZ = new Station(24, "Schwyz");
        private static final Station SIO = new Station(25, "Sion");
        private static final Station STG = new Station(27, "Saint-Gall");
        private static final Station VAD = new Station(28, "Vaduz");
        private static final Station WAS = new Station(29, "Wassen");
        private static final Station WIN = new Station(30, "Winterthour");
        private static final Station ZUR = new Station(33, "Zürich");

        // Stations - countries
        private static final Station DE1 = new Station(34, "Allemagne");
        private static final Station DE2 = new Station(35, "Allemagne");
        private static final Station DE3 = new Station(36, "Allemagne");
        private static final Station DE4 = new Station(37, "Allemagne");
        private static final Station DE5 = new Station(38, "Allemagne");
        private static final Station AT1 = new Station(39, "Autriche");
        private static final Station AT2 = new Station(40, "Autriche");
        private static final Station AT3 = new Station(41, "Autriche");
        private static final Station IT1 = new Station(42, "Italie");
        private static final Station IT2 = new Station(43, "Italie");
        private static final Station IT3 = new Station(44, "Italie");
        private static final Station IT4 = new Station(45, "Italie");
        private static final Station IT5 = new Station(46, "Italie");
        private static final Station FR1 = new Station(47, "France");
        private static final Station FR2 = new Station(48, "France");
        private static final Station FR3 = new Station(49, "France");
        private static final Station FR4 = new Station(50, "France");

        // Countries
        private static final List<Station> DE = List.of(DE1, DE2, DE3, DE4, DE5);
        private static final List<Station> AT = List.of(AT1, AT2, AT3);
        private static final List<Station> IT = List.of(IT1, IT2, IT3, IT4, IT5);
        private static final List<Station> FR = List.of(FR1, FR2, FR3, FR4);

        // Routes (without double routes!)
        private static final List<Route> ALL_ROUTES = List.of(
                new Route("AT1_STG_1", AT1, STG, 4, Route.Level.UNDERGROUND, null),
                new Route("AT2_VAD_1", AT2, VAD, 1, Route.Level.UNDERGROUND, Color.RED),
                new Route("BAD_BAL_1", BAD, BAL, 3, Route.Level.UNDERGROUND, Color.RED),
                new Route("BAD_OLT_1", BAD, OLT, 2, Route.Level.OVERGROUND, Color.VIOLET),
                new Route("BAD_ZUR_1", BAD, ZUR, 1, Route.Level.OVERGROUND, Color.YELLOW),
                new Route("BAL_DE1_1", BAL, DE1, 1, Route.Level.UNDERGROUND, Color.BLUE),
                new Route("BAL_DEL_1", BAL, DEL, 2, Route.Level.UNDERGROUND, Color.YELLOW),
                new Route("BAL_OLT_1", BAL, OLT, 2, Route.Level.UNDERGROUND, Color.ORANGE),
                new Route("BRI_IT5_1", BRI, IT5, 3, Route.Level.UNDERGROUND, Color.GREEN),
                new Route("BRI_LOC_1", BRI, LOC, 6, Route.Level.UNDERGROUND, null),
                new Route("BRI_SIO_1", BRI, SIO, 3, Route.Level.UNDERGROUND, Color.BLACK),
                new Route("BRI_WAS_1", BRI, WAS, 4, Route.Level.UNDERGROUND, Color.RED),
                new Route("BRU_COI_1", BRU, COI, 5, Route.Level.UNDERGROUND, null),
                new Route("BRU_DAV_1", BRU, DAV, 4, Route.Level.UNDERGROUND, Color.BLUE),
                new Route("BRU_IT2_1", BRU, IT2, 2, Route.Level.UNDERGROUND, Color.GREEN),
                new Route("COI_DAV_1", COI, DAV, 2, Route.Level.UNDERGROUND, Color.VIOLET),
                new Route("COI_SAR_1", COI, SAR, 1, Route.Level.UNDERGROUND, Color.WHITE),
                new Route("COI_WAS_1", COI, WAS, 5, Route.Level.UNDERGROUND, null),
                new Route("DAV_AT3_1", DAV, AT3, 3, Route.Level.UNDERGROUND, null),
                new Route("DAV_IT1_1", DAV, IT1, 3, Route.Level.UNDERGROUND, null)
        );

        private static final List<Ticket> ALL_TICKETS = List.of(
                // City-to-city tickets
                new Ticket(BAL, BER, 5),
                new Ticket(BAL, BRI, 10),
                new Ticket(BAL, STG, 8),
                new Ticket(BER, COI, 10),
                new Ticket(BER, LUG, 12),
                new Ticket(BER, SCZ, 5),
                new Ticket(BER, ZUR, 6),
                new Ticket(FRI, LUC, 5),
                new Ticket(GEN, BAL, 13),
                new Ticket(GEN, BER, 8),
                new Ticket(GEN, SIO, 10),
                new Ticket(GEN, ZUR, 14),
                new Ticket(INT, WIN, 7),
                new Ticket(KRE, ZUR, 3));
    }

}
