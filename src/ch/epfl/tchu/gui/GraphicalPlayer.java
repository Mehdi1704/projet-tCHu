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
import static ch.epfl.tchu.gui.ActionHandlers.*;
import static javafx.application.Platform.isFxApplicationThread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Le joueur graphique
 *
 * @author Mehdi Bouchoucha (314843)
 * @author Ali Ridha Mrad (314529)
 */
public class GraphicalPlayer {
    private final ObservableList<Text> listOfTexts;
    private final ObservableGameState observableGameState;
    private final Stage stage;
    private final ObjectProperty<ClaimRouteHandler> claimRoute;
    private final ObjectProperty<DrawTicketsHandler> drawTickets;
    private final ObjectProperty<DrawCardHandler> drawCard;

    /**
     * Constructeur de GraphicalPlayer.
     *
     * @param playerId    Identité du joueur
     * @param playerNames Map reliant l'identité du joueur à son nom.
     */
    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> playerNames) {
        listOfTexts = FXCollections.observableArrayList();
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
     * Cette méthode permet de mettre à jour l'état public de la partie et l'état du joueur à l'état observable du joueur.
     *
     * @param publicGameState état public de la partie
     * @param playerState     état du joueur .
     */
    public void setState(PublicGameState publicGameState, PlayerState playerState) {
        assert isFxApplicationThread();
        observableGameState.setState(publicGameState, playerState);
    }

    /**
     * Ajoute un message au bas des informations, sur le déroulement de la partie.
     *
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
     *
     * @param drawTicketsHandler gestionnaires d'action pour tirage de ticket.
     * @param claimRouteHandler  gestionnaires d'action pour s'emparer d'une route.
     * @param drawCardHandler    gestionnaires d'action pour tirage de carte.
     */

    public void startTurn(DrawTicketsHandler drawTicketsHandler,
                          ClaimRouteHandler claimRouteHandler,
                          DrawCardHandler drawCardHandler) {
        assert isFxApplicationThread();

        claimRoute.set((route, sorted) -> {
            setToNull();
            claimRouteHandler.onClaimRoute(route, sorted);
        });

        if (observableGameState.canDrawTickets()) {
            drawTickets.set(() -> {
                setToNull();
                drawTicketsHandler.onDrawTickets();
            });

        }
        if (observableGameState.canDrawCards()) {
            drawCard.set((index) -> {
                setToNull();
                drawCardHandler.onDrawCard(index);
            });
        }
    }

    /**
     * Méthode qui ouvre une fenêtre ,permettant au joueur de faire son choix de ticket.
     *
     * @param bagOfTickets         Multiensemble contenant cinq ou trois billets que le joueur peut choisir.
     * @param chooseTicketsHandler gestionnaires d'action pour choix de ticket.
     */
    public void chooseTickets(SortedBag<Ticket> bagOfTickets,
                              ChooseTicketsHandler chooseTicketsHandler) {
        assert isFxApplicationThread();
        Preconditions.checkArgument((bagOfTickets.size() == 5) || (bagOfTickets.size() == 3));
        int minSelectedTickets = bagOfTickets.size() - DISCARDABLE_TICKETS_COUNT;
        String chooseTicketsString = String.format(StringsFr.CHOOSE_TICKETS,
                minSelectedTickets,
                StringsFr.plural(minSelectedTickets));
        ListView<Ticket> listView = new ListView<>(FXCollections.observableList(bagOfTickets.toList()));
        Button confirmButton = new Button(StringsFr.CHOOSE);

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

        Stage chooseTickets = createWindow(box, StringsFr.TICKETS_CHOICE);
        chooseTickets.show();

    }

    /**
     * Méthode qui autorise le joueur a choisir une carte wagon/locomotive :
     * soit l'une des cinq dont la face est visible, soit celle du sommet de la pioche.
     * Elle va être appelée lorsque le joueur a déjà tiré une première carte et doit maintenant tirer la seconde.
     *
     * @param drawCardHandler gestionnaires d'action pour tirage de carte.
     */
    public void drawCard(DrawCardHandler drawCardHandler) {
        assert isFxApplicationThread();
        drawCard.set((index -> {
            setToNull();
                drawCardHandler.onDrawCard(index);

            }));
    }

    /**
     *
     * Cette méthode permet d'ouvrir une fenêtre présentant au joueur une liste de multiensembles de cartes,
     * étant les cartes initiales qu'il peut utiliser pour s'emparer d'une route,
     * et ainsi permet au joueur de choisir avec quel multiensemble de carte il désire s'emparer de la route.
     *
     * @param listOfBags liste de multiensembles de cartes(cartes initiales qu'il peut utiliser pour s'emparer d'une route).
     * @param chooseCardsHandler un gestionnaire d'action pour choix de cartes.
     */
    public void chooseClaimCards(List<SortedBag<Card>> listOfBags,
                                 ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();

        ListView<SortedBag<Card>> listView = new ListView<>(FXCollections.observableList(listOfBags));
        listView.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));

        Button confirmButton = new Button(StringsFr.CHOOSE);

        confirmButton.disableProperty().bind(Bindings.greaterThan(1,
                Bindings.size(listView.getSelectionModel().getSelectedItems())));
        confirmButton.setOnAction(e -> {
            confirmButton.getScene().getWindow().hide();
            SortedBag<Card> cards = SortedBag.of(listView.getSelectionModel().getSelectedItem());
            chooseCardsHandler.onChooseCards(cards);
        });
        Text text = new Text(StringsFr.CHOOSE_CARDS);
        TextFlow textFlow = new TextFlow();
        textFlow.getChildren().add(text);
        VBox box = new VBox();
        box.getChildren().addAll(textFlow, listView, confirmButton);

        Stage chooseTickets = createWindow(box, StringsFr.CARDS_CHOICE);
        chooseTickets.show();

    }

    /**
     *
     * Cette méthode permet d'ouvrir une fenêtre présentant au joueur une liste de multiensembles de cartes,
     * étant plus précisement les cartes additionnelles qu'il peut utiliser pour s'emparer d'un tunnel,
     * lui permettant ainsi de faire son choix.
     *
     *
     * @param listOfBags Une liste de multiensembles de cartes,
     *                 (cartes additionnelles qu'il peut utiliser pour s'emparer d'un tunnel).
     * @param chooseCardsHandler gestionnaire d'action pour choix de cartes.
     */
    public void chooseAdditionalCards(List<SortedBag<Card>> listOfBags,
                                      ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();

        ListView<SortedBag<Card>> listView = new ListView<>(FXCollections.observableList(listOfBags));
        listView.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));

        Button confirmButton = new Button(StringsFr.CHOOSE);

        confirmButton.disableProperty().bind(Bindings.greaterThan(0,
                Bindings.size(listView.getSelectionModel().getSelectedItems())));
        confirmButton.setOnAction(e -> {
            confirmButton.getScene().getWindow().hide();


            if (listView.getSelectionModel().getSelectedItems().size() == 0) {
                chooseCardsHandler.onChooseCards(SortedBag.of());
            } else if (listView.getSelectionModel().getSelectedItems().size() == 1) {
                AudioPlayer.play("/ziw.wav",false);
                chooseCardsHandler.onChooseCards(listView.getSelectionModel().getSelectedItem());
            }
        });
        Text text = new Text(StringsFr.CHOOSE_ADDITIONAL_CARDS);
        TextFlow textFlow = new TextFlow();
        textFlow.getChildren().add(text);
        VBox box = new VBox();
        box.getChildren().addAll(textFlow, listView, confirmButton);

        Stage chooseTickets = createWindow(box, StringsFr.CARDS_CHOICE);
        chooseTickets.show();


    }

    private Stage createWindow(VBox box, String title) {
        Scene scene = new Scene(new BorderPane(box));
        scene.getStylesheets().add("chooser.css");
        Stage chooseStage = new Stage(StageStyle.UTILITY);
        chooseStage.initOwner(stage);
        chooseStage.initModality(Modality.WINDOW_MODAL);
        chooseStage.setScene(scene);
        chooseStage.setOnCloseRequest(Event::consume);
        chooseStage.initOwner(stage);
        chooseStage.initModality(Modality.WINDOW_MODAL);
        chooseStage.setTitle(title);
        return chooseStage;
    }

    private void setToNull(){
        claimRoute.set(null);
        drawCard.set(null);
        drawTickets.set(null);
    }


    public static class CardBagStringConverter extends StringConverter<SortedBag<Card>> {

        /**
         * transforme le multiensemble de cartes en chaînes de caratères.
         * @param object  multiensembles de cartes
         * @return une chaine de caractère sous le format souhaité.
         */
        @Override
        public String toString(SortedBag<Card> object) {
            ArrayList<String> listOfNames = new ArrayList<>();
            for (Card c : object.toSet()) {
                int nbOfCards = object.countOf(c);
                listOfNames.add(nbOfCards + " " + Info.cardName(c, nbOfCards));
            }
            return String.join(StringsFr.AND_SEPARATOR, listOfNames);
        }

        /**
         * lève une exception de type UnsupportedOperationException car jamais utilisée dans ce contexte.
         * @param string chaine de caractères.
         * @throw lance une exception du type UnsupportedOperationException.
         */
        @Override
        public SortedBag<Card> fromString(String string) {
            throw new UnsupportedOperationException();
        }
    }
}
