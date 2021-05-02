package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

import static ch.epfl.tchu.game.Constants.FACE_UP_CARD_SLOTS;
import static ch.epfl.tchu.game.Constants.TOTAL_CARDS_COUNT;

public class ObservableGameState {

    private final PlayerState playerState;
    private final PublicGameState publicGameState;

    // Groupe des propriétés concernant l'état public de la partie
    private final IntegerProperty poucentageTicket;
    private final IntegerProperty pourcentageCard;
    private final List<ObjectProperty<Card>> faceUpCards = new ArrayList<>();
    private final Map<Route, ObjectProperty<PlayerId>> routeObjectPropertyMap = new HashMap<>();

    // Groupe des propriétés concernant l'état public de chacun des joueurs
    private final Map<PlayerId, IntegerProperty> numberOfTicketsMap = new EnumMap<>(PlayerId.class);
    private final Map<PlayerId, IntegerProperty> numberOfCardsMap = new EnumMap<>(PlayerId.class);
    private final Map<PlayerId, IntegerProperty> numberOfWagonsMap = new EnumMap<>(PlayerId.class);
    private final Map<PlayerId, IntegerProperty> numberOfPointsOfConstructionMap = new EnumMap<>(PlayerId.class);

    // Groupe des propriétés concernant l'état privé du joueur
    private final List<Ticket> listOfTicket = new ArrayList<>(); //ticket
    private final Map<Card, IntegerProperty> numberOfEachTypeOfCardMap = new EnumMap<>(Card.class);
    private final Map<Route, BooleanProperty> canTakeRouteMap = new HashMap<>();


    public ObservableGameState(PlayerId PlayerId) {

        playerState = null;
        publicGameState = null;

        poucentageTicket = new SimpleIntegerProperty(0);
        pourcentageCard = new SimpleIntegerProperty(0);

        for (int slot : FACE_UP_CARD_SLOTS) {
            faceUpCards.add(new SimpleObjectProperty<Card>(null));
        }

        Card.ALL.forEach(carte -> numberOfEachTypeOfCardMap.put(carte, new SimpleIntegerProperty(0)));

        ChMap.routes().forEach(e -> {
            routeObjectPropertyMap.put(e, new SimpleObjectProperty<>(null));
            canTakeRouteMap.put(e, new SimpleBooleanProperty(false));
        });


        PlayerId.ALL.forEach(playerId -> {
            numberOfTicketsMap.put(playerId, new SimpleIntegerProperty(0));
            numberOfCardsMap.put(playerId, new SimpleIntegerProperty(0));
            numberOfWagonsMap.put(playerId, new SimpleIntegerProperty(0));
            numberOfPointsOfConstructionMap.put(playerId, new SimpleIntegerProperty(0));

        });
    }

    public void setState(PublicGameState publicGameState, PlayerState playerState) {

        //TODO pourcentages
        //TODO castage
        poucentageTicket.set((publicGameState.ticketsCount() * 100) / ChMap.tickets().size());
        pourcentageCard.set((publicGameState.cardState().deckSize() * 100) / TOTAL_CARDS_COUNT);

        // Face Up Cards
        for (int slot : FACE_UP_CARD_SLOTS) {
            Card newCard = publicGameState.cardState().faceUpCard(slot);
            faceUpCards.get(slot).set(newCard);
        }

        PlayerId.ALL.forEach(playerId -> {
            PublicPlayerState tempPlayerState = publicGameState.playerState(playerId);
            // route Object Property Map
            List<Route> listOfRoutes = tempPlayerState.routes();
            for (Route r : routeObjectPropertyMap.keySet()) {//TODO verifier
                if (listOfRoutes.contains(r)) routeObjectPropertyMap.put(r, new SimpleObjectProperty<>(playerId));
            }
            // number Of Tickets Map
            numberOfTicketsMap.put(playerId, new SimpleIntegerProperty(
                    tempPlayerState.ticketCount()));
            // number Of Cards Map
            numberOfCardsMap.put(playerId, new SimpleIntegerProperty(
                    tempPlayerState.cardCount()));
            // number Of Wagons Map
            numberOfWagonsMap.put(playerId, new SimpleIntegerProperty(
                    tempPlayerState.carCount()));
            //numberOfWagonsMap.forEach((p,i)-> i.set(tempPlayerState.carCount()));
            // number Of Points Of Construction Map
            numberOfPointsOfConstructionMap.put(playerId, new SimpleIntegerProperty(
                    tempPlayerState.claimPoints()));
        });
        // list Of Ticket
        listOfTicket.addAll(playerState.tickets().toList());

        //TODO sortedbag manip
        playerState.cards();
        //numberOfEachTypeOfCardMap.forEach();

        // can Take Route Map
        canTakeRouteMap.forEach((r, v) -> v.set(playerState.canClaimRoute(r)));

    }

    public PublicGameState getPublicGameState() {
        return publicGameState;
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    //_____________________GETTERS DE PUBLIC GAME STATE_____________________
    public ReadOnlyIntegerProperty poucentageTicket() {
        return poucentageTicket;
    }

    public ReadOnlyIntegerProperty pourcentageCard() {
        return pourcentageCard;
    }

    public ReadOnlyObjectProperty<Card> faceUpCard(int slot) {
        return faceUpCards.get(slot);
    }

    public ReadOnlyObjectProperty<PlayerId> routeObjectPropertyMap(Route route) {
        return routeObjectPropertyMap.get(route);
    }

    //_____________________GETTERS PUBLIC PLAYER STATE_____________________
    public ReadOnlyIntegerProperty numberofTickets(PlayerId playerId) {
        return numberOfTicketsMap.get(playerId);
    }

    public ReadOnlyIntegerProperty numberOfCards(PlayerId playerId) {
        return numberOfCardsMap.get(playerId);
    }

    public ReadOnlyIntegerProperty numberOfWagons(PlayerId playerId) {
        return numberOfWagonsMap.get(playerId);
    }

    public ReadOnlyIntegerProperty numberOfPointsOfConstruction(PlayerId playerId) {
        return numberOfPointsOfConstructionMap.get(playerId);
    }

    //_____________________GETTERS PLAYER STATE___________________________

    //TODO listOfTicket getter verifier
    public ObservableList<Ticket> tickets() {
        return FXCollections.unmodifiableObservableList(
                FXCollections.observableList(listOfTicket));
    }

    public ReadOnlyIntegerProperty numberOfEachTypeOfCards(Card card) {
        return numberOfEachTypeOfCardMap.get(card);
    }

    public ReadOnlyBooleanProperty canTakeRoute(Route route) {
        return canTakeRouteMap.get(route);
    }

}
