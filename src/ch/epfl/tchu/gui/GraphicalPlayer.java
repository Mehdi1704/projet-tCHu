package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Box;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.gui.ActionHandlers.*;

import java.util.List;
import java.util.Map;

public class GraphicalPlayer {
    private final PlayerId playerId;
    private final Map<PlayerId, String> playerNames;
    private final ObservableGameState observableGameState;
    private final ObservableList<Text> listOfTexts;

    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> playerNames, ObservableList<Text> listOfTexts){
        this.playerId = playerId;
        this.playerNames = playerNames;
        observableGameState = new ObservableGameState(playerId);
        this.listOfTexts = listOfTexts;
    }

    public void setState(PublicGameState publicGameState, PlayerState playerState){

        observableGameState.setState(publicGameState,playerState);
    }

    public void receiveInfo(String message){
        Text text = new Text(message);
        listOfTexts.add(text);
        if (listOfTexts.size() > 5) listOfTexts.remove(0);
        // l'ajoutant au bas des informations sur le déroulement de la partie,
        // qui sont présentées dans la partie inférieure de la vue des informations
        // pour mémoire, cette vue ne doit contenir que les cinq derniers messages reçus
    }

    public void startTurn(DrawTicketsHandler drawTicketsHandler,
                          ClaimRouteHandler claimRouteHandler,
                          DrawCardHandler drawCardHandler){
    if (observableGameState.canDrawTickets()) drawTicketsHandler.onDrawTickets();
    if (observableGameState.canDrawCards()) drawCardHandler.onDrawCard(2);//TODO


    }

    public void chooseTickets(SortedBag<Ticket> bagOfTickets,
                              ChooseTicketsHandler chooseTicketsHandler){
        // ouvre une fenêtre similaire à celle des figures 3 et 4,
        // permettant au joueur de faire son choix; une fois celui-ci confirmé,
        // le gestionnaire de choix est appelé avec ce choix en argument
        chooseTicketsHandler.onChooseTickets(bagOfTickets);


    }

    public void drawCard(DrawCardHandler drawCardHandler){
        // autorise le joueur a choisir une carte wagon/locomotive,
        // soit l'une des cinq dont la face est visible, soit celle du sommet de la pioche;
        // une fois que le joueur a cliqué sur l'une de ces cartes, le gestionnaire
        // est appelé avec le choix du joueur ; cette méthode est destinée à être
        // appelée lorsque le joueur a déjà tiré une première carte et doit maintenant tirer la seconde

    }

    public void chooseClaimCards(List<SortedBag<Card>> listOfBags,
                                 ChooseCardsHandler chooseCardsHandler){

    }

    public void chooseAdditionalCards(List<SortedBag<Card>> listOfBags,
                                      ChooseCardsHandler chooseCardsHandler){

    }

    private Node createWindow() {
        Stage stage = new Stage(StageStyle.UTILITY);

        Node mapView = MapViewCreator
                .createMapView(observableGameState, claim, chooseCards);
        Node cardsView = DecksViewCreator
                .createCardsView(observableGameState, drawTickets, drawCard);
        Node handView = DecksViewCreator
                .createHandView(observableGameState);
        Node infoView = InfoViewCreator
                .createInfoView(PLAYER_1, playerNames, gameState, infos);

        BorderPane mainPane = new BorderPane(mapView, null, cardsView, handView, infoView);
        Scene scene = new Scene(mainPane);

        return null;
    }
}
