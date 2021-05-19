package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

public class ClientMain extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        List<String> args = getParameters().getRaw();

        GraphicalPlayerAdapter player = new GraphicalPlayerAdapter();
        RemotePlayerClient client = new RemotePlayerClient(player, args.get(0), Integer.parseInt(args.get(1)));
        new Thread(client::run).start();
    }
}
