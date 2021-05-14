package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.PlayerId;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Map;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

public class testwind extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        Map<PlayerId, String> playerNames2 =
                Map.of(PLAYER_1, "Ada", PLAYER_2, "Charles");
        ObservableList<Text> infos2 = FXCollections.observableArrayList();

        GraphicalPlayer player = new GraphicalPlayer(PlayerId.PLAYER_1, playerNames2, infos2);

        player.chooseTickets(SortedBag.of(ChMap.tickets().subList(0, 5)), (e) -> System.out.println("choisi"));
    }





}
