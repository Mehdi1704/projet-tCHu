package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

/**
 * Client de jeu
 *
 * @author Mehdi Bouchoucha (314843)
 * @author Ali Ridha Mrad (314529)
 */
public class ClientMain extends Application {

    /**
     * Lance le jeu du côté du client
     *
     * @param args arguments du client : hote et adresse IP
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    /**
     * Lance le jeu du côté du client
     * Se connecte à un serveur afin de commencer la partie
     *
     * @param primaryStage Fenetre de jeu
     * @throws Exception Erreur
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        List<String> args = getParameters().getRaw();
        GraphicalPlayerAdapter player = new GraphicalPlayerAdapter();
        RemotePlayerClient client = new RemotePlayerClient(player, args.get(0), Integer.parseInt(args.get(1)));
        new Thread(client::run).start();
    }
}
