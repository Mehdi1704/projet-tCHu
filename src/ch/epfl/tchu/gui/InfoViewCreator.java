package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Map;

class InfoViewCreator {

    public static Node createInfoView(PlayerId playerId,
                                      Map<PlayerId, String> playerNames,
                                      ObservableGameState observableGameState,
                                      ObservableList<Text> textList){

        VBox vBox = new VBox();
        vBox.getStylesheets().addAll("info.css","colors.css");
        VBox vbox2 = new VBox();
        vbox2.setId("player-stats");
        Separator separator = new Separator();
        separator.setOrientation(Orientation.HORIZONTAL);

        vbox2.getChildren().add(PlayerStats(observableGameState,PlayerId.PLAYER_1,playerNames));
        vbox2.getChildren().add(PlayerStats(observableGameState,PlayerId.PLAYER_2,playerNames));

        TextFlow textFlow = new TextFlow();
        textFlow.setId("game-info");
        Bindings.bindContent(textFlow.getChildren(),textList);

        vBox.getChildren().addAll(vbox2,separator,textFlow);

        return vBox;
    }

    private static TextFlow PlayerStats(ObservableGameState observableGameState,PlayerId playerId, Map<PlayerId, String> playerNames){
        TextFlow textFlow = new TextFlow();
        textFlow.getStyleClass().add(playerId.name());
        Circle circle = new Circle(5);
        circle.getStyleClass().add("filled");

        Text text = new Text();
        text.textProperty().bind(Bindings.format(StringsFr.PLAYER_STATS,playerNames.get(playerId),
                observableGameState.numberofTickets(playerId),
                observableGameState.numberOfCards(playerId),
                observableGameState.numberOfWagons(playerId),
                observableGameState.numberOfPointsOfConstruction(playerId)));

        textFlow.getChildren().addAll(circle,text);
        return textFlow;

    }

}
