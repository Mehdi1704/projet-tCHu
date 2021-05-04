package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import static ch.epfl.tchu.gui.ActionHandler.*;

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
                                     ObjectProperty<ClaimRouteHandler> claimRouteHandler,
                                     CardChooser cardChooser) {

        CardChooser firstChoice = (options, handler) -> {
            handler.onChooseCards(options.get(0));
        };

        Pane paneFond = new Pane();
        paneFond.getStylesheets().addAll("map.css", "colors.css");
        paneFond.getChildren().add(new ImageView());

        ChMap.routes().forEach(route -> {
            Group r1 = GroupRoute(route, observableGameState);
            r1.disableProperty().bind(claimRouteHandler.isNull().or(observableGameState.canTakeRoute(route).not()));

            r1.setOnMouseClicked(mouseEvent -> {

                if (check(observableGameState.getPlayerState(), route).size() == 1) {
                    claimRouteHandler.get().onClaimRoute(route, observableGameState.possibleClaimCards(route).get(0));
                } else if (check(observableGameState.getPlayerState(), route).size() >= 1) {
                    ChooseCardsHandler chooseCardsH =
                            chosenCards -> claimRouteHandler.get().onClaimRoute(route, chosenCards);
                    cardChooser.chooseCards(observableGameState.possibleClaimCards(route), chooseCardsH);

                }
            });
            
            paneFond.getChildren().add(r1);
        });


        return paneFond;
    }


    private static Group GroupRoute(Route route, ObservableGameState observableGameState) {
        Group theRoute = new Group();
        String playerId = "";
        if (PlayerId.PLAYER_1.equals(observableGameState.routeObjectPropertyMap(route).get())) {
            playerId = PlayerId.PLAYER_1.name();
        } else if (PlayerId.PLAYER_2.equals(observableGameState.routeObjectPropertyMap(route).get())) {
            playerId = PlayerId.PLAYER_2.name();
        }

        // a voir.
        observableGameState.routeObjectPropertyMap(route).addListener((p, o, n) ->
                theRoute.getStyleClass().set(3, n.name()));

        String type = route.level().name();
        String color = Objects.isNull(route.color()) ? "NEUTRAL" : route.color().name();
        theRoute.setId(route.id());
        theRoute.getStyleClass().addAll("route", type, color, playerId);


        for (int i = 0; i < route.length(); i++) {
            theRoute.getChildren().add(GroupCase(i + 1, route, observableGameState));
        }
        return theRoute;
    }


    private static Group GroupCase(int index, Route route, ObservableGameState observableGameState) {
        Group theCase = new Group();
        Group wagon = GroupWagon();
        theCase.setId(route.id() + "_" + index);
        //voie
        Rectangle rect = new Rectangle(36, 12);
        rect.getStyleClass().addAll("track", "filled");
        theCase.getChildren().add(rect);

        observableGameState.routeObjectPropertyMap(route).addListener((p, o, n) ->
                wagon.visibleProperty().set(!Objects.isNull(n)));


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
                         ChooseCardsHandler handler);
    }

    private static List<SortedBag<Card>> check(PlayerState playerState, Route route) {
        if (Objects.isNull(playerState)) {
            return List.of();
        } else {
            return playerState.possibleClaimCards(route);
        }
    }
}
