package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.*;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import javax.management.monitor.GaugeMonitor;

class DecksViewCreator {

    public static Node createHandView(ObservableGameState observableGameState){

        HBox box = new HBox();
        box.getStylesheets().addAll("decks.css","colors.css");

        ListView<Ticket> listView = new ListView<>();
        listView.setId("tickets");

        HBox box2 = new HBox();
        box2.setId("hand-pane");

        Card.ALL.forEach(card -> box2.getChildren().add(cardView(card.name())));

        box.getChildren().addAll(listView, box2);
        return box;
    }

    //TODO mettre observableGameState
    public static Node createCardsView(ObservableGameState gameState,
                                       ObjectProperty<ActionHandler.DrawTicketsHandler> ticketHandler,
                                       ObjectProperty<ActionHandler.DrawCardHandler> cardHandler){

        VBox box = new VBox();
        box.getStylesheets().addAll("decks.css","colors.css");
        box.getChildren().add(gaugeButton(25,"Billets"));
        box.setId("card-pane");
        for (int i = 0 ; i < 5 ; i++){
            box.getChildren().add(cardView(""));
        }
        box.getChildren().add(gaugeButton(40,"Cartes"));
        return box;
    }

    private static StackPane cardView(String cardName){
        if (cardName.equals("LOCOMOTIVE")){
            cardName="NEUTRAL";
        }
        //TODO count
        Rectangle rect1 = new Rectangle(60,90);
        rect1.getStyleClass().add("outside");

        Rectangle rect2 = new Rectangle(40,70);
        rect2.getStyleClass().addAll("filled", "inside");

        Rectangle rect3 = new Rectangle(40,70);
        rect3.getStyleClass().add("train-image");

        StackPane stackPane = new StackPane();
        stackPane.getStyleClass().addAll(cardName,"card");
        stackPane.getChildren().addAll(rect1,rect2,rect3);

        return stackPane;
    }

    private static Button gaugeButton(int deckSize, String title){
        int buttonHeight = 5;
        int buttonWidth = 50;

        Rectangle background = new Rectangle(buttonWidth,buttonHeight);
        background.getStyleClass().add("background");
        //TODO pourcentage
        //int percentageButtonWidth = (deckSize * buttonWidth) / 100;

        Rectangle foreground = new Rectangle(deckSize,buttonHeight);

        foreground.getStyleClass().add("foreground");

        Button gaugedButton = new Button(title);
        gaugedButton.getStyleClass().add("gauged");

        Group group = new Group();
        group.getChildren().addAll(background,foreground);

        gaugedButton.setGraphic(group);
        return gaugedButton;
    }


}
