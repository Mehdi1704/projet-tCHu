package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.*;

import static ch.epfl.tchu.gui.ActionHandlers.*;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.Objects;

/**
 * Vue des pioches, et des cartes et tickets du joueur
 *
 * @author Mehdi Bouchoucha (314843)
 * @author Ali Ridha Mrad (314529)
 */
class DecksViewCreator {

    /**
     * Création de la vue des cartes et tickets du joueur
     *
     * @param observableGameState Etat de jeu observable
     * @return Noeud représentant les élements de jeu du joueur
     */
    public static Node createHandView(ObservableGameState observableGameState) {

        HBox box = new HBox();
        box.getStylesheets().addAll("decks.css", "colors.css");
        // Création de la liste de tickets
        ListView<Ticket> listView = new ListView<>(observableGameState.tickets());
        listView.setId("tickets");

        HBox box2 = new HBox();
        box2.setId("hand-pane");
        // Création des cartes en main du joueur
        Card.ALL.forEach(card -> {
            ReadOnlyIntegerProperty count = observableGameState.numberOfEachTypeOfCards(card);
            count.addListener((observable, oldValue, newValue) -> System.out.println("Types of cards: " + newValue));
            Text counter = new Text();
            counter.getStyleClass().add("count");
            counter.textProperty().bind(Bindings.convert(count));
            counter.visibleProperty().bind(Bindings.greaterThan(count, 1));
            StackPane stackPane = cardView(card);
            stackPane.visibleProperty().bind(Bindings.greaterThan(count, 0));
            stackPane.getChildren().add(counter);
            box2.getChildren().add(stackPane);
        });

        box.getChildren().addAll(listView, box2);
        return box;
    }

    /**
     * Création de la vue des pioches de cartes (visibles ou pas) et de tickets
     *
     * @param observableGameState Etat de jeu observable
     * @param ticketHandler       Gestionnaire des tickets
     * @param cardHandler         Gestionnaire des cartes
     * @return Noeud représentant les élements piochables visibles aux deux joueurs
     */
    public static Node createCardsView(ObservableGameState observableGameState,
                                       ObjectProperty<DrawTicketsHandler> ticketHandler,
                                       ObjectProperty<DrawCardHandler> cardHandler) {

        VBox cardsView = new VBox();
        cardsView.getStylesheets().addAll("decks.css", "colors.css");
        cardsView.setId("card-pane");
        // Creation du bouton pour les tickets
        ReadOnlyIntegerProperty percentageTickets = observableGameState.poucentageTicket();
        Button ticketButton = gaugeButton(percentageTickets, StringsFr.TICKETS);
        ticketButton.disableProperty().bind(ticketHandler.isNull());
        ticketButton.setOnAction(e -> ticketHandler.get().onDrawTickets());
        cardsView.getChildren().add(ticketButton);
        // Creation des cartes face visible
        for (int i = 0; i < 5; i++) {//TODO optimiser boucle
            int slot = i;
            ReadOnlyObjectProperty<Card> faceUpCard = observableGameState.faceUpCard(i);
            StackPane card = cardView(faceUpCard.get());
            faceUpCard.addListener((observable, oldValue, newValue) -> {
                System.out.println("Face up card: " + newValue);
                card.getStyleClass().set(0, newValue.equals(Card.LOCOMOTIVE) ? "NEUTRAL" : newValue.name());
            });
            card.disableProperty().bind(cardHandler.isNull());
            card.setOnMouseClicked(e -> cardHandler.get().onDrawCard(slot));
            cardsView.getChildren().add(card);
        }

        // Creation du bouton pour la pioche de cartes
        ReadOnlyIntegerProperty percentageCards = observableGameState.pourcentageCard();
        Button cardsButton = gaugeButton(percentageCards, StringsFr.CARDS);
       // cardsButton.disabledProperty().isEqualTo(cardHandler.isNull());
        cardsButton.disableProperty().bind(cardHandler.isNull());
        cardsButton.setOnAction(e -> cardHandler.get().onDrawCard(-1));
        cardsView.getChildren().add(cardsButton);

        return cardsView;
    }

    private static StackPane cardView(Card card) {

        String cardName = "";
        if (!Objects.isNull(card)){
            cardName = card.equals(Card.LOCOMOTIVE) ? "NEUTRAL" : card.name();
        }

        Rectangle rect1 = new Rectangle(60, 90);
        rect1.getStyleClass().add("outside");

        Rectangle rect2 = new Rectangle(40, 70);
        rect2.getStyleClass().addAll("filled", "inside");

        Rectangle rect3 = new Rectangle(40, 70);
        rect3.getStyleClass().add("train-image");

        StackPane stackPane = new StackPane();
        stackPane.getStyleClass().addAll(cardName, "card");
        stackPane.getChildren().addAll(rect1, rect2, rect3);

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
