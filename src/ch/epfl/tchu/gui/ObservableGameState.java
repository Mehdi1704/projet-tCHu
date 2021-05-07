package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.stream.Collectors;

import static ch.epfl.tchu.game.Constants.FACE_UP_CARD_SLOTS;
import static ch.epfl.tchu.game.Constants.TOTAL_CARDS_COUNT;
/**
 * @author Mehdi Bouchoucha (314843)
 * @author Ali Ridha Mrad (314529)
 */


public class ObservableGameState {

    private final PlayerId playerId;
    private PlayerState playerStateAtt;
    private PublicGameState publicGameStateAtt;

    // Groupe des propriétés concernant l'état public de la partie
    private final IntegerProperty percentageTicket;
    private final IntegerProperty percentageCard;
    private final List<ObjectProperty<Card>> faceUpCards = new ArrayList<>();
    private final Map<Route, ObjectProperty<PlayerId>> routeObjectPropertyMap = new HashMap<>();

    // Groupe des propriétés concernant l'état public de chacun des joueurs
    private final Map<PlayerId, IntegerProperty> numberOfTicketsMap = new EnumMap<>(PlayerId.class);
    private final Map<PlayerId, IntegerProperty> numberOfCardsMap = new EnumMap<>(PlayerId.class);
    private final Map<PlayerId, IntegerProperty> numberOfWagonsMap = new EnumMap<>(PlayerId.class);
    private final Map<PlayerId, IntegerProperty> numberOfPointsOfConstructionMap = new EnumMap<>(PlayerId.class);

    // Groupe des propriétés concernant l'état privé du joueur
    private final ListProperty<Ticket> listOfTicket = new SimpleListProperty<>();
    private final Map<Card, IntegerProperty> numberOfEachTypeOfCardMap = new EnumMap<>(Card.class);
    private final Map<Route, BooleanProperty> canTakeRouteMap = new HashMap<>();

    /**
     * Constructeur initialisant nos valeurs
     * @param PlayerId identité du joueur.
     */

    public ObservableGameState(PlayerId PlayerId) {
        this.playerId= PlayerId;
        playerStateAtt = null;
        publicGameStateAtt = null;

        percentageTicket = new SimpleIntegerProperty(0);
        percentageCard = new SimpleIntegerProperty(0);

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

        listOfTicket.set(null);
    }

    /**
     * methode permettant de met à jour la totalité des propriétés des éléments du jeu .
     *
     *
     * @param publicGameState l'état publique de la partie.
     * @param playerState état complet du joueur .
     */

    public void setState(PublicGameState publicGameState, PlayerState playerState) {

        playerStateAtt = playerState;
        publicGameStateAtt = publicGameState;

        percentageTicket.set(((publicGameState.ticketsCount() * 100) / ChMap.tickets().size()));
        percentageCard.set(((publicGameState.cardState().deckSize() * 100) / TOTAL_CARDS_COUNT));

        // Face Up Cards
        for (int slot : FACE_UP_CARD_SLOTS) {
            Card newCard = publicGameState.cardState().faceUpCard(slot);
            faceUpCards.get(slot).set(newCard);
        }

        PlayerId.ALL.forEach(playerId -> {
            PublicPlayerState tempPlayerState = publicGameState.playerState(playerId);
            // route Object Property Map
            List<Route> listOfRoutes = tempPlayerState.routes();
            for (Route r : routeObjectPropertyMap.keySet()) {
                if (listOfRoutes.contains(r)) routeObjectPropertyMap.get(r).set(playerId);
            }
            // number Of Tickets Map
            numberOfTicketsMap.get(playerId).set(tempPlayerState.ticketCount());
            // number Of Cards Map
            numberOfCardsMap.get(playerId).set(tempPlayerState.cardCount());
            // number Of Wagons Map
            numberOfWagonsMap.get(playerId).set(tempPlayerState.carCount());
            // number Of Points Of Construction Map
            numberOfPointsOfConstructionMap.get(playerId).set(tempPlayerState.claimPoints());
        });
        // list Of Ticket
        listOfTicket.setValue(FXCollections.observableList(playerState.tickets().toList()));

        //Sortedbag map.
        numberOfEachTypeOfCardMap.forEach((c,v)-> v.set(playerState.cards().countOf(c)));

        List<Route> takenRoutes = publicGameState.claimedRoutes();
                  Set<List<Station>> setOfListOfStationTaken = takenRoutes
                    .stream()
                    .map(Route::stations)
                    .collect(Collectors.toSet());

        // can Take Route Map
        canTakeRouteMap.forEach((r, v) -> v.set(playerState.canClaimRoute(r)
                            && publicGameState.currentPlayerId().equals(playerId)
                            && !(setOfListOfStationTaken.contains(r.stations()))
        ));
    }

    /**
     * les différents getter .
     */

    public PublicGameState getPublicGameStateAtt() {
        return publicGameStateAtt;
    }

    public PlayerState getPlayerStateAtt() {
        return playerStateAtt;
    }

    //_____________________GETTERS DE PUBLIC GAME STATE_____________________
    public ReadOnlyIntegerProperty poucentageTicket() {
        return percentageTicket;
    }

    public ReadOnlyIntegerProperty pourcentageCard() {
        return percentageCard;
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
    public ObservableList<Ticket> tickets() {
        return FXCollections.unmodifiableObservableList(listOfTicket);
    }

    public ReadOnlyIntegerProperty numberOfEachTypeOfCards(Card card) {
        return numberOfEachTypeOfCardMap.get(card);
    }

    public ReadOnlyBooleanProperty canTakeRoute(Route route) {
        return canTakeRouteMap.get(route);
    }

    public boolean canDrawTickets() {
        return publicGameStateAtt.canDrawTickets();
    }

    public boolean canDrawCards() {
        return publicGameStateAtt.canDrawCards();
    }

    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        return playerStateAtt.possibleClaimCards(route);
    }

    public PlayerId getPlayerId() {
        return playerId;
    }

}
