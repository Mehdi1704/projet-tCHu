package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

import static ch.epfl.tchu.game.Constants.DISCARDABLE_TICKETS_COUNT;
import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.gui.ActionHandlers.*;
import static javafx.application.Platform.isFxApplicationThread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GraphicalPlayerChonb {
    private final PlayerId playerId;
    private final Map<PlayerId, String> playerNames;
    private final ObservableList<Text> listOfTexts;
    private final ObservableGameState observableGameState;
    private final Stage stage;
    private final ObjectProperty<ClaimRouteHandler> claimRoute;
    private final ObjectProperty<DrawTicketsHandler> drawTickets;
    private final ObjectProperty<DrawCardHandler> drawCard;


    public GraphicalPlayerChonb(PlayerId playerId, Map<PlayerId, String> playerNames, ObservableList<Text> listOfTexts) {
        this.playerId = playerId;
        this.playerNames = playerNames;
        this.listOfTexts = listOfTexts;
        observableGameState = new ObservableGameState(playerId);
        claimRoute = new SimpleObjectProperty<>(null);
        drawTickets = new SimpleObjectProperty<>(null);
        drawCard = new SimpleObjectProperty<>(null);

        Node mapView = MapViewCreator.createMapView(observableGameState, claimRoute, this::chooseClaimCards);
        Node cardsView = DecksViewCreator.createCardsView(observableGameState, drawTickets, drawCard);
        Node handView = DecksViewCreator.createHandView(observableGameState);
        Node infoView = InfoViewCreator.createInfoView(playerId, playerNames, observableGameState, listOfTexts);

        BorderPane mainPane = new BorderPane(mapView, null, cardsView, handView, infoView);

        stage = new Stage();
        stage.setTitle("tCHu" + " \u2014 " + playerNames.get(playerId));
        stage.setScene(new Scene(mainPane));
        stage.show();

    }

    /**
     * cette méthode permet de mettre à jour l'état public de la partie et l'état du joueur à l'état observable du joueur.
     * @param publicGameState état public de la partie
     * @param playerState état du joueur .
     */
    public void setState(PublicGameState publicGameState, PlayerState playerState) {
        assert isFxApplicationThread();
        observableGameState.setState(publicGameState, playerState);
    }

    /**
     * Ajoute au bas des informations sur le déroulement de la partie un message.
     * @param message message à afficher.
     */
    public void receiveInfo(String message) {
        assert isFxApplicationThread();
        Text text = new Text(message);
        listOfTexts.add(text);
        if (listOfTexts.size() > 5) listOfTexts.remove(0);
    }

    /**
     * Permet au joueur d'effectuer un type d'action.
     * @param drawTicketsHandler gestionnaires d'action pour tirage de ticket.
     * @param claimRouteHandler gestionnaires d'action pour s'emparer d'une route.
     * @param drawCardHandler gestionnaires d'action pour tirage de carte.
     */

    public void startTurn(DrawTicketsHandler drawTicketsHandler,
                          ClaimRouteHandler claimRouteHandler,
                          DrawCardHandler drawCardHandler) {
        assert isFxApplicationThread();

        claimRoute.set((route,sorted)->{
            claimRoute.set(null);
            drawCard.set(null);
            drawTickets.set(null);
            claimRouteHandler.onClaimRoute(route,sorted);
        });

        if (observableGameState.canDrawTickets()){
            drawTickets.set(()->{
                claimRoute.set(null);
                drawCard.set(null);
                drawTickets.set(null);
                drawTicketsHandler.onDrawTickets();
            } );

        }
        if (observableGameState.canDrawCards()){
            drawCard.set((index)->{
                claimRoute.set(null);
                drawCard.set(null);
                drawTickets.set(null);
                drawCardHandler.onDrawCard(index);
            });
        }


    }

    public void chooseTickets(SortedBag<Ticket> bagOfTickets,
                              ChooseTicketsHandler chooseTicketsHandler) {
        assert isFxApplicationThread();
        Preconditions.checkArgument((bagOfTickets.size() == 5 ) || (bagOfTickets.size() == 3)) ;

        int minSelectedTickets = bagOfTickets.size()-DISCARDABLE_TICKETS_COUNT;
        String chooseTicketsString = String.format(StringsFr.CHOOSE_TICKETS,
                minSelectedTickets,
                StringsFr.plural(minSelectedTickets));
        ListView<Ticket> listView = new ListView<>(FXCollections.observableList(bagOfTickets.toList()));
        Button confirmButton = new Button("Choisir");

        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        confirmButton.disableProperty().bind(Bindings.greaterThan(minSelectedTickets,
                Bindings.size(listView.getSelectionModel().getSelectedItems())));
        confirmButton.setOnAction(e -> {
            confirmButton.getScene().getWindow().hide();
            chooseTicketsHandler.onChooseTickets(SortedBag.of(listView.getSelectionModel().getSelectedItems()));
        });
        Text text = new Text(chooseTicketsString);
        TextFlow textFlow = new TextFlow();
        textFlow.getChildren().add(text);
        VBox box = new VBox();
        box.getChildren().addAll(textFlow, listView, confirmButton);

        Stage chooseTickets = createWindow(box);
        chooseTickets.initOwner(stage);
        chooseTickets.initModality(Modality.WINDOW_MODAL);
        chooseTickets.setTitle(StringsFr.TICKETS_CHOICE);
        chooseTickets.show();


    }

    public void drawCard(DrawCardHandler drawCardHandler) {
        assert isFxApplicationThread();

        if(observableGameState.canDrawCards()) {
            drawCard.set((index -> {
                claimRoute.set(null);
                drawCard.set(null);
                drawTickets.set(null);
                drawCardHandler.onDrawCard(index);

            }));
        }


    }

    public void chooseClaimCards(List<SortedBag<Card>> listOfBags,
                                 ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();

        ListView<SortedBag<Card>> listView = new ListView<>(FXCollections.observableList(listOfBags));
        listView.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));

        //Stage chooseTickets = createWindow(StringsFr.CARDS_CHOICE, StringsFr.CHOOSE_CARDS, list, 1);

    }

    public void chooseAdditionalCards(List<SortedBag<Card>> listOfBags,
                                      ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();

        ListView<SortedBag<Card>> list = new ListView<>(FXCollections.observableList(listOfBags));
        list.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));

        //TODO sortedbag
        //TODO listview polymorphique
        //Stage chooseTickets = createWindow(StringsFr.CARDS_CHOICE, StringsFr.CHOOSE_ADDITIONAL_CARDS, list, 0);

    }

    private Stage createWindow(VBox box) {




        Scene scene = new Scene(new BorderPane(box));
        scene.getStylesheets().add("chooser.css");
        Stage chooseStage = new Stage(StageStyle.UTILITY);
        chooseStage.initOwner(stage);
        chooseStage.initModality(Modality.WINDOW_MODAL);
        chooseStage.setScene(scene);
        chooseStage.setOnCloseRequest(Event::consume);

        return chooseStage;
    }

    public static class CardBagStringConverter extends StringConverter<SortedBag<Card>> {

        @Override
        public String toString(SortedBag<Card> object) {
            ArrayList<String> listOfNames = new ArrayList<>();
            for (Card c : object.toSet()) {
                int nbOfCards = object.countOf(c);
                listOfNames.add(nbOfCards + " " + Info.cardName(c, nbOfCards));
            }
            return String.join(StringsFr.AND_SEPARATOR, listOfNames);
        }

        @Override
        public SortedBag<Card> fromString(String string) {
            throw new UnsupportedOperationException();
        }
    }
}
