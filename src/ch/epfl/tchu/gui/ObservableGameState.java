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
    private PlayerState playerStateAtt = null;
    private PublicGameState publicGameStateAtt = null;

    // Groupe des propriétés concernant l'état public de la partie
    private final IntegerProperty percentageTicket  = new SimpleIntegerProperty();
    private final IntegerProperty percentageCard  = new SimpleIntegerProperty();
    private final List<ObjectProperty<Card>> faceUpCards = faceUpCardSetter();
    private final Map<Route, ObjectProperty<PlayerId>> routeObjectPropertyMap = routeObjectPropertyMapSetter();

    // Groupe des propriétés concernant l'état public de chacun des joueurs
    private final Map<PlayerId, IntegerProperty> numberOfTicketsMap = enumMapSetter();
    private final Map<PlayerId, IntegerProperty> numberOfCardsMap = enumMapSetter();
    private final Map<PlayerId, IntegerProperty> numberOfWagonsMap = enumMapSetter();
    private final Map<PlayerId, IntegerProperty> numberOfPointsOfConstructionMap = enumMapSetter();

    // Groupe des propriétés concernant l'état privé du joueur
    private final ObservableList<Ticket> listOfTicket = FXCollections.observableArrayList();
    private final Map<Card, IntegerProperty> numberOfEachTypeOfCardMap = numberOfEachTypeOfCardMapSetter();
    private final Map<Route, BooleanProperty> canTakeRouteMap = canTakeRouteMapSetter();


    /**
     * Constructeur initialisant nos valeurs
     * @param PlayerId identité du joueur.
     */

    public ObservableGameState(PlayerId PlayerId) {
        this.playerId= PlayerId;
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
        listOfTicket.setAll(playerState.tickets().toList());

        //Sortedbag map.
        numberOfEachTypeOfCardMap.forEach((c,v)-> v.set(playerState.cards().countOf(c)));

        Set<List<Station>> setOfListOfStationTaken =
                    publicGameState.claimedRoutes()
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
     * Les différents getter
     */
    //_____________________GETTERS DE PUBLIC GAME STATE_____________________
    public ReadOnlyIntegerProperty percentageTicket() {
        return percentageTicket;
    }

    public ReadOnlyIntegerProperty percentageCard() {
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

    private Map<PlayerId, IntegerProperty> enumMapSetter(){
        EnumMap<PlayerId, IntegerProperty> map = new EnumMap<>(PlayerId.class);
        PlayerId.ALL.forEach(playerId -> map.put(playerId, new SimpleIntegerProperty(0)));
        return map;
    }

    private Map<Route, ObjectProperty<PlayerId>> routeObjectPropertyMapSetter(){
        Map<Route, ObjectProperty<PlayerId>> map = new HashMap<>();
        ChMap.routes().forEach(route -> map.put(route, new SimpleObjectProperty<>(null)));
        return map;
    }

    private Map<Route, BooleanProperty> canTakeRouteMapSetter(){
        Map<Route, BooleanProperty> map = new HashMap<>();
        ChMap.routes().forEach(e -> map.put(e, new SimpleBooleanProperty(false)));
        return map;
    }

    private List<ObjectProperty<Card>> faceUpCardSetter() {
        List<ObjectProperty<Card>> list = new ArrayList<>();
        for (int ignored : FACE_UP_CARD_SLOTS) list.add(new SimpleObjectProperty<>(null));
        return list;
    }

    private Map<Card, IntegerProperty> numberOfEachTypeOfCardMapSetter(){
        Map<Card, IntegerProperty> map = new HashMap<>();
        Card.ALL.forEach(card -> map.put(card, new SimpleIntegerProperty(0)));
        return map;
    }

}
