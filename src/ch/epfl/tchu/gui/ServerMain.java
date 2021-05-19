package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Game;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.RemotePlayerClient;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;
import static java.nio.charset.StandardCharsets.US_ASCII;

public class ServerMain extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(5108)) {

            Socket socket = serverSocket.accept();

            List<String> args = getParameters().getRaw();

            Map<PlayerId, String> playerNames =
                    Map.of(PLAYER_1, args.get(0), PLAYER_2, args.get(1));

            BufferedReader r = new BufferedReader(
                                    new InputStreamReader(socket.getInputStream(), US_ASCII));
            BufferedWriter w = new BufferedWriter(
                                    new OutputStreamWriter(socket.getOutputStream(), US_ASCII));

            Player graphicalPlayer = new GraphicalPlayerAdapter();
            Player remotePlayerProxy = new RemotePlayerProxy(socket, r, w);

            Map<PlayerId, Player> players =
                    Map.of(PLAYER_1, graphicalPlayer,
                            PLAYER_2, remotePlayerProxy);


            new Thread(()->Game.play(players, playerNames, SortedBag.of(ChMap.tickets()), new Random())).start();
        }
    }
}



