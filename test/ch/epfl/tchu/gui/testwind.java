package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.PlayerId;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.text.Text;

import java.util.Map;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

public class testwind {

    public static void main(String[] args) {


        Map<PlayerId, String> playerNames =
                Map.of(PLAYER_1, "Ada", PLAYER_2, "Charles");
        ObservableList<Text> infos = FXCollections.observableArrayList();

        GraphicalPlayer player = new GraphicalPlayer(PlayerId.PLAYER_1,playerNames,infos);

      //  player.chooseTickets(SortedBag.of(ChMap.tickets().subList(0,5)),(e)-> System.out.println("choisi"));
    }





}
