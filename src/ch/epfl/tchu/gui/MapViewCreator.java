package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.Objects;


class MapViewCreator {

    public static Node createMapView(ObservableGameState observableGameState,
                                     ObjectProperty<ActionHandler.ClaimRouteHandler> arg2,
                                     CardChooser cardChooser) {

        Pane paneFond = new Pane();
        paneFond.getStylesheets().addAll("map.css", "colors.css");
        paneFond.getChildren().add(new ImageView());

        ChMap.routes().forEach(route -> {
            Group r1 = GroupRoute(route);
            paneFond.getChildren().add(r1);
        });
        return paneFond;
    }



    private static Group GroupRoute(Route route) {
        Group theRoute = new Group();
        String type = route.level().name();
        String color = Objects.isNull(route.color()) ? "NEUTRAL" : route.color().name();
        theRoute.setId(route.id());
        theRoute.getStyleClass().addAll("route", type, color);

        for (int i = 0; i < route.length(); i++) {
            theRoute.getChildren().add(GroupCase(i + 1, route));
        }
        return theRoute;
    }


    private static Group GroupCase(int index, Route route) {
        Group theCase = new Group();
        theCase.setId(route.id() + "_" + index);
        //voie
        Rectangle rect = new Rectangle(36, 12);
        rect.getStyleClass().addAll("track", "filled");
        theCase.getChildren().add(rect);

        theCase.getChildren().add(GroupWagon());
        return theCase;
    }

    // création d'un wagon ;
    private static Group GroupWagon() {
        Group wagon = new Group();
        wagon.getStyleClass().add("car");

        Rectangle rect = new Rectangle(36, 12);
        rect.getStyleClass().add("filled");
        Circle cercle1 = new Circle(12, 6, 3);
        Circle cercle2 = new Circle(24, 6, 3);

        wagon.getChildren().addAll(rect, cercle1, cercle2);
        return wagon;
    }

    @FunctionalInterface
    interface CardChooser {
        void chooseCards(List<SortedBag<Card>> options,
                         ActionHandler.ChooseCardsHandler handler);
    }
}
