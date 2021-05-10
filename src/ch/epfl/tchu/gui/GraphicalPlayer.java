package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
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

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.gui.ActionHandlers.*;
import static javafx.application.Platform.isFxApplicationThread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GraphicalPlayer {
    private final PlayerId playerId;
    private final Map<PlayerId, String> playerNames;
    private final ObservableList<Text> listOfTexts;
    private final ObservableGameState observableGameState;
    private final Stage stage;
    private final ObjectProperty<ClaimRouteHandler> claimRoute;
    private final ObjectProperty<DrawTicketsHandler> drawTickets;
    private final ObjectProperty<DrawCardHandler> drawCard;


    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> playerNames, ObservableList<Text> listOfTexts) {
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
        Node infoView = InfoViewCreator.createInfoView(PLAYER_1, playerNames, observableGameState, listOfTexts);

        BorderPane mainPane = new BorderPane(mapView, null, cardsView, handView, infoView);
        //creation
        stage = new Stage();
        stage.setTitle("tCHu" + " \u2014 " + playerNames.get(playerId));
        stage.setScene(new Scene(mainPane));
        stage.show();

    }

    public void setState(PublicGameState publicGameState, PlayerState playerState) {
        assert isFxApplicationThread();
        observableGameState.setState(publicGameState, playerState);
    }

    public void receiveInfo(String message) {
        assert isFxApplicationThread();
        Text text = new Text(message);
        listOfTexts.add(text);
        if (listOfTexts.size() > 5) listOfTexts.remove(0);
        // l'ajoutant au bas des informations sur le déroulement de la partie,
        // qui sont présentées dans la partie inférieure de la vue des informations
        // pour mémoire, cette vue ne doit contenir que les cinq derniers messages reçus
    }

    public void startTurn(DrawTicketsHandler drawTicketsHandler,
                          ClaimRouteHandler claimRouteHandler,
                          DrawCardHandler drawCardHandler) {
        assert isFxApplicationThread();
        if (observableGameState.canDrawTickets()) drawTicketsHandler.onDrawTickets();
        if (observableGameState.canDrawCards()) drawCardHandler.onDrawCard(0);//TODO


    }

    public void chooseTickets(SortedBag<Ticket> bagOfTickets,
                              ChooseTicketsHandler chooseTicketsHandler) {
        assert isFxApplicationThread();

        Preconditions.checkArgument(bagOfTickets.size() <= 5);
        Preconditions.checkArgument(bagOfTickets.size() >= 3);

        String chooseTicketsString = String.format(StringsFr.CHOOSE_TICKETS, 2, StringsFr.plural(2));
        String title = StringsFr.TICKETS_CHOICE;
        ListView<Text> listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        Stage chooseTickets = createWindow(title, listView);
        chooseTickets.show();


        //listView.setCellFactory(v -> bagOfTickets.toList().toString());



    }

    public void drawCard(DrawCardHandler drawCardHandler) {
        assert isFxApplicationThread();
        // autorise le joueur a choisir une carte wagon/locomotive,
        // soit l'une des cinq dont la face est visible, soit celle du sommet de la pioche;
        // une fois que le joueur a cliqué sur l'une de ces cartes, le gestionnaire
        // est appelé avec le choix du joueur; cette méthode est destinée à être
        // appelée lorsque le joueur a déjà tiré une première carte et doit maintenant tirer la seconde

    }

    public void chooseClaimCards(List<SortedBag<Card>> listOfBags,
                                 ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();

        ListView<SortedBag<Card>> list = new ListView<>();
        list.setCellFactory(v ->
                new TextFieldListCell<>(new CardBagStringConverter()));



    }

    public void chooseAdditionalCards(List<SortedBag<Card>> listOfBags,
                                      ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();

    }

    private Stage createWindow(String title, ListView<Text> listView) {

        Button confirmButton = new Button("Confirmer");
        Text text = new Text(title);
        TextFlow textFlow = new TextFlow();
        textFlow.getChildren().add(text);
        VBox box = new VBox();
        box.getChildren().addAll(confirmButton, textFlow, listView);
        //TODO listview


        Scene scene = new Scene(new BorderPane(box));
        scene.getStylesheets().add("chooser.css");
        Stage chooseStage = new Stage(StageStyle.UTILITY);
        chooseStage.initOwner(stage);
        chooseStage.initModality(Modality.WINDOW_MODAL);
        chooseStage.setScene(scene);



        return null;
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
