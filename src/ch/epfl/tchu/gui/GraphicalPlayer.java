package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import static ch.epfl.tchu.gui.ActionHandlers.*;

import java.util.List;
import java.util.Map;

public class GraphicalPlayer {
    private final PlayerId playerId;
    private final Map<PlayerId, String> playerNames;
    private final ObservableGameState observableGameState;

    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> playerNames){
        this.playerId = playerId;
        this.playerNames = playerNames;
        observableGameState = new ObservableGameState(playerId);

    }

    public void setState(PublicGameState publicGameState, PlayerState playerState){

        observableGameState.setState(publicGameState,playerState);
    }

    public void receiveInfo(String message){
        // l'ajoutant au bas des informations sur le déroulement de la partie,
        // qui sont présentées dans la partie inférieure de la vue des informations
        // pour mémoire, cette vue ne doit contenir que les cinq derniers messages reçus
    }

    public void startTurn(DrawTicketsHandler drawTicketsHandler,
                          ClaimRouteHandler claimRouteHandler,
                          DrawCardHandler drawCardHandler){


    }

    public void chooseTickets(SortedBag<Ticket> bagOfTickets,
                              ChooseTicketsHandler chooseTicketsHandler){
        // ouvre une fenêtre similaire à celle des figures 3 et 4,
        // permettant au joueur de faire son choix; une fois celui-ci confirmé,
        // le gestionnaire de choix est appelé avec ce choix en argument
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
}
