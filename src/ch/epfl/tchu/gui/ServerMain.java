package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Game;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
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

/**
 * Serveur de jeu
 *
 * @author Mehdi Bouchoucha (314843)
 * @author Ali Ridha Mrad (314529)
 */
public class ServerMain extends Application {

    /**
     * Lance le jeu du côté du serveur
     *
     * @param args arguments du client : noms des joueurs 1 et 2
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    /**
     * Lance le jeu du côté du serveur
     * Attend la connexion d'un client avant de lancer la partie
     *
     * @param primaryStage Fenetre de jeu
     * @throws Exception Erreur
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(5108)) {



            Socket socket = serverSocket.accept();

            List<String> args = getParameters().getRaw();

            Map<PlayerId, String> playerNames;

            if(args.isEmpty()){
                playerNames = Map.of(PLAYER_1, "Ada", PLAYER_2, "Charles");
            }else if(args.size()==1){
                playerNames  = Map.of(PLAYER_1, args.get(0), PLAYER_2, "Charles");
            }else {
                playerNames = Map.of(PLAYER_1, args.get(0), PLAYER_2, args.get(1));
            }



            BufferedReader r = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), US_ASCII));
            BufferedWriter w = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(), US_ASCII));

            Player graphicalPlayer = new GraphicalPlayerAdapter();
            Player remotePlayerProxy = new RemotePlayerProxy(socket, r, w);

            Map<PlayerId, Player> players =
                    Map.of(PLAYER_1, graphicalPlayer,
                            PLAYER_2, remotePlayerProxy);
            //AudioPlayer.play("/mine.wav", true);
            new Thread(() -> Game.play(players, playerNames, SortedBag.of(ChMap.tickets()), new Random())).start();
        }
    }
}