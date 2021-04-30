package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.*;
import javafx.beans.property.*;

import java.util.*;

import static ch.epfl.tchu.game.Constants.FACE_UP_CARD_SLOTS;

public class ObservableGameState {

    private PlayerState playerState ;
    private PublicGameState publicGameState;

    //groupe des propriétés concernant l'état public de la partie
    private final IntegerProperty poucentageTicket;
    private final IntegerProperty pourcentageCard;
    private final List<ObjectProperty<Card>> faceUpCards = new ArrayList<>();
    private final Map<Route,ObjectProperty<PlayerId>>  routeObjectPropertyMap = new HashMap<>();

    //groupe des propriétés concernant l'état public de chacun des joueurs
    private final Map<PlayerId,IntegerProperty> numberOfTicketMap  = new EnumMap<>(PlayerId.class);
    private final Map<PlayerId,IntegerProperty> numberOfCardMap = new EnumMap<>(PlayerId.class);
    private final Map<PlayerId,IntegerProperty> numberOfWagonMap = new EnumMap<>(PlayerId.class);
    private final Map<PlayerId,IntegerProperty> numberOfPointOfConstructionMap = new EnumMap<>(PlayerId.class);

    //groupe des propriétés concernant l'état privé du joueur
    private final List<ObjectProperty<Ticket>> listOfTicket = new ArrayList<>(); //ticket
    private final Map<Card,IntegerProperty> numberOfEachTypeOfCardMap = new EnumMap<>(Card.class);
    private final Map<Route, BooleanProperty> canTakeRouteMap = new HashMap<>();


    public ObservableGameState(PlayerId PlayerId){

        playerState = null ;
        publicGameState = null ;

        poucentageTicket= new SimpleIntegerProperty(0);
        pourcentageCard = new SimpleIntegerProperty(0);

        for (int slot: FACE_UP_CARD_SLOTS) {
            faceUpCards.add(new SimpleObjectProperty<>(null));
        }
        // FACE_UP_CARD_SLOTS.forEach(index -> faceUpCards.add(new SimpleObjectProperty<>(null)));

        Card.ALL.forEach(carte->numberOfEachTypeOfCardMap.put(carte,new SimpleIntegerProperty(0)));


        ChMap.routes().forEach(e->{
            routeObjectPropertyMap.put(e, new SimpleObjectProperty<>(null));
            canTakeRouteMap.put(e,new SimpleBooleanProperty(false));
        });


        PlayerId.ALL.forEach(playerId -> {
            numberOfTicketMap.put(playerId,new SimpleIntegerProperty(0));
            numberOfCardMap.put(playerId,new SimpleIntegerProperty(0));
            numberOfWagonMap.put(playerId,new SimpleIntegerProperty(0));
            numberOfPointOfConstructionMap.put(playerId,new SimpleIntegerProperty(0));

        });


    }

    public ObservableGameState setState(PublicGameState publicGameState, PlayerState playerState){
        return this;
    }
//TODO getter , list setState.

}
