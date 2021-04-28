package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.List;

class MapViewCreator {
    public static Node createMapView(ObservableGameState observableGameState,
                                     ObjectProperty<ActionHandler.ClaimRouteHandler> arg2,
                                     CardChooser cardChooser){
        CardChooser firstChoice = (options, handler) -> {
            handler.onChooseCards(options.get(0));
        };
        Image map = new Image("map.css");
        ImageView mapImageView = new ImageView();
        mapImageView.setImage(map);
        GridPane gridPane = new GridPane();
        gridPane.add(mapImageView,0,0);


        return gridPane;
    }

    @FunctionalInterface
    interface CardChooser {
        void chooseCards(List<SortedBag<Card>> options,
                         ActionHandler.ChooseCardsHandler handler);
    }

   // private GridPane
}
