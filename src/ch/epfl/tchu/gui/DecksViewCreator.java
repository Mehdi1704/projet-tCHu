package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;


class DecksViewCreator {

    public static Node createHandView(ObservableGameState observableGameState) {

        HBox box = new HBox();
        box.getStylesheets().addAll("decks.css", "colors.css");

        ListView<Ticket> listView = new ListView<>();
        listView.setId("tickets");

        HBox box2 = new HBox();
        box2.setId("hand-pane");


        Card.ALL.forEach(card -> box2.getChildren().add(cardView(card.name())));

        //observableGameState.getPlayerState().cards().toList().

        box.getChildren().addAll(listView, box2);
        return box;
    }

    //TODO mettre observableGameState
    public static Node createCardsView(ObservableGameState gameState,
                                       ObjectProperty<ActionHandler.DrawTicketsHandler> ticketHandler,
                                       ObjectProperty<ActionHandler.DrawCardHandler> cardHandler) {

        VBox box = new VBox();
        box.getStylesheets().addAll("decks.css", "colors.css");

        box.getChildren().add(gaugeButton(gameState.poucentageTicket(), "Billets"));
        box.setId("card-pane");
        for (int i = 0; i < 5; i++) {
            box.getChildren().add(cardView(gameState.faceUpCard(i).getName()));
        }
        box.getChildren().add(gaugeButton(gameState.pourcentageCard(), "Cartes"));
        return box;
    }

    private static StackPane cardView(String cardName, ObservableGameState observableGameState) {
        if (cardName.equals("LOCOMOTIVE")) {
            cardName = "NEUTRAL";
        }
        //TODO count
        Text counter = new Text();
        counter.getStyleClass().add("count");

        Rectangle rect1 = new Rectangle(60, 90);
        rect1.getStyleClass().add("outside");

        Rectangle rect2 = new Rectangle(40, 70);
        rect2.getStyleClass().addAll("filled", "inside");

        Rectangle rect3 = new Rectangle(40, 70);
        rect3.getStyleClass().add("train-image");

        StackPane stackPane = new StackPane();
        stackPane.getStyleClass().addAll(cardName, "card");
        stackPane.getChildren().addAll(rect1, rect2, rect3);

        observableGameState.numberOfEachTypeOfCards()
        stackPane.visibleProperty().bind(Bindings.greaterThan(count, 0));

        return stackPane;
    }

    private static Button gaugeButton(ReadOnlyIntegerProperty deckSize, String title) {
        int buttonHeight = 5;
        int buttonWidth = 50;

        Rectangle background = new Rectangle(buttonWidth, buttonHeight);
        background.getStyleClass().add("background");

        Rectangle foreground = new Rectangle(buttonWidth, buttonHeight);
        foreground.widthProperty().bind(
                deckSize.multiply(50).divide(100));

        foreground.getStyleClass().add("foreground");

        Button gaugedButton = new Button(title);
        gaugedButton.getStyleClass().add("gauged");

        Group group = new Group();
        group.getChildren().addAll(background, foreground);

        gaugedButton.setGraphic(group);
        return gaugedButton;
    }


}
