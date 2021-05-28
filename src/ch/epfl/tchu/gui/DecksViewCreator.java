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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.Objects;

/**
 * Vue des pioches, et des cartes et tickets du joueur.
 *
 * @author Mehdi Bouchoucha (314843)
 * @author Ali Ridha Mrad (314529)
 */
class DecksViewCreator {

    /**
     * Création de la vue des cartes et tickets du joueur.
     *
     * @param observableGameState Etat de jeu observable.
     * @return Noeud représentant les élements de jeu du joueur.
     */
    public static Node createHandView(ObservableGameState observableGameState) {

        HBox box = new HBox();
        box.getStylesheets().addAll("decks.css", "colors.css");
        // Création de la table de tickets
        TableView<TicketState> table = new TableView<>(observableGameState.getListOfTicketState());
        table.setId("tickets");
        TableColumn<TicketState, String> ticketCol = new TableColumn<>("Ticket");
        ticketCol.setMinWidth(300);
        ticketCol.setCellValueFactory(cell -> cell.getValue().getTicketFirstName());
        TableColumn<TicketState, String> tookCol = new TableColumn<>("État");
        tookCol.setCellValueFactory(cell -> cell.getValue().doneTicketProperty());
        table.getColumns().add(ticketCol);
        table.getColumns().add(tookCol);

        HBox box2 = new HBox();
        box2.setId("hand-pane");
        // Création des cartes en main du joueur
        Card.ALL.forEach(card -> {
            ReadOnlyIntegerProperty count = observableGameState.numberOfEachTypeOfCards(card);
            Text counter = new Text();
            counter.getStyleClass().add("count");
            counter.textProperty().bind(Bindings.convert(count));
            counter.visibleProperty().bind(Bindings.greaterThan(count, 1));
            StackPane stackPane = cardView(card);
            stackPane.visibleProperty().bind(Bindings.greaterThan(count, 0));
            stackPane.getChildren().add(counter);
            box2.getChildren().add(stackPane);
        });
        box.getChildren().addAll(table, box2);
        return box;
    }

    /**
     * Création de la vue des pioches de cartes (visibles ou pas) et de tickets.
     *
     * @param observableGameState Etat de jeu observable.
     * @param ticketHandler       Gestionnaire des tickets.
     * @param cardHandler         Gestionnaire des cartes.
     * @return Noeud représentant les élements piochables visibles aux deux joueurs.
     */
    public static Node createCardsView(ObservableGameState observableGameState,
                                       ObjectProperty<DrawTicketsHandler> ticketHandler,
                                       ObjectProperty<DrawCardHandler> cardHandler) {

        VBox cardsView = new VBox();
        cardsView.getStylesheets().addAll("decks.css", "colors.css");
        cardsView.setId("card-pane");
        // Creation du bouton pour les tickets
        ReadOnlyIntegerProperty percentageTickets = observableGameState.percentageTicket();
        Button ticketButton = gaugeButton(percentageTickets, StringsFr.TICKETS);
        ticketButton.disableProperty().bind(ticketHandler.isNull());
        ticketButton.setOnAction(e -> {
            ticketHandler.get().onDrawTickets();
            AudioPlayer.play("/cardraw.wav",false);

        });

        cardsView.getChildren().add(ticketButton);
        // Creation des cartes face visible

        for (int i = 0; i < 5; i++) {
            int slot = i;
            ReadOnlyObjectProperty<Card> faceUpCard = observableGameState.faceUpCard(i);
            StackPane card = cardView(faceUpCard.get());
            faceUpCard.addListener((observable, oldValue, newValue) ->
                    card.getStyleClass().set(0, newValue.equals(Card.LOCOMOTIVE) ? "NEUTRAL" : newValue.name()));
            card.disableProperty().bind(cardHandler.isNull());
            card.setOnMouseClicked(e -> {
                        cardHandler.get().onDrawCard(slot);
                        AudioPlayer.play("/cardraw.wav",false);
                    }
            );
            cardsView.getChildren().add(card);
        }

        // Creation du bouton pour la pioche de cartes
        ReadOnlyIntegerProperty percentageCards = observableGameState.percentageCard();
        Button cardsButton = gaugeButton(percentageCards, StringsFr.CARDS);
        cardsButton.disableProperty().bind(cardHandler.isNull());
        cardsButton.setOnAction(e -> {
                    cardHandler.get().onDrawCard(-1);
                    AudioPlayer.play("/cardraw.wav",false);
                }
        );
        cardsView.getChildren().add(cardsButton);

        return cardsView;
    }

    private static StackPane cardView(Card card) {
        int rectWidth = 40;
        int rectHeight = 70;
        int outsideMargin = 20;

        String cardName = "";
        if (!Objects.isNull(card)) {
            cardName = card.equals(Card.LOCOMOTIVE) ? "NEUTRAL" : card.name();
        }

        Rectangle rect1 = new Rectangle(rectWidth + outsideMargin, rectHeight + outsideMargin);
        rect1.getStyleClass().add("outside");

        Rectangle rect2 = new Rectangle(rectWidth, rectHeight);
        rect2.getStyleClass().addAll("filled", "inside");

        Rectangle rect3 = new Rectangle(rectWidth, rectHeight);
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
