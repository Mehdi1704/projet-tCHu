package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.gui.ActionHandlers.*;
import static javafx.application.Platform.isFxApplicationThread;

import java.util.List;
import java.util.Map;

public class GraphicalPlayer {
    private final PlayerId playerId;
    private final Map<PlayerId, String> playerNames;
    private final ObservableList<Text> listOfTexts;
    private final ObservableGameState observableGameState;
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
        Stage stage = new Stage();
        stage.setTitle("tCHu" + " \u2014 " + playerNames.get(playerId));
        stage.setScene(new Scene(mainPane));
        stage.show();

    }

    public void setState(PublicGameState publicGameState, PlayerState playerState) {
        assert isFxApplicationThread();
        observableGameState.setState(publicGameState, playerState);
    }

    public void receiveInfo(String message) {
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
        if (observableGameState.canDrawTickets()) drawTicketsHandler.onDrawTickets();
        if (observableGameState.canDrawCards()) drawCardHandler.onDrawCard(0);//TODO


    }

    public void chooseTickets(SortedBag<Ticket> bagOfTickets,
                              ChooseTicketsHandler chooseTicketsHandler) {
        // ouvre une fenêtre similaire à celle des figures 3 et 4,
        // permettant au joueur de faire son choix; une fois celui-ci confirmé,
        // le gestionnaire de choix est appelé avec ce choix en argument
        Preconditions.checkArgument(bagOfTickets.size() <= 5);
        Preconditions.checkArgument(bagOfTickets.size() >= 3);

        chooseTicketsHandler.onChooseTickets(bagOfTickets);


    }

    public void drawCard(DrawCardHandler drawCardHandler) {
        // autorise le joueur a choisir une carte wagon/locomotive,
        // soit l'une des cinq dont la face est visible, soit celle du sommet de la pioche;
        // une fois que le joueur a cliqué sur l'une de ces cartes, le gestionnaire
        // est appelé avec le choix du joueur ; cette méthode est destinée à être
        // appelée lorsque le joueur a déjà tiré une première carte et doit maintenant tirer la seconde

    }

    public void chooseClaimCards(List<SortedBag<Card>> listOfBags,
                                 ChooseCardsHandler chooseCardsHandler) {

    }

    public void chooseAdditionalCards(List<SortedBag<Card>> listOfBags,
                                      ChooseCardsHandler chooseCardsHandler) {

    }

    private Node createWindow() {
        Stage stage = new Stage(StageStyle.UTILITY);


        return null;
    }
}
