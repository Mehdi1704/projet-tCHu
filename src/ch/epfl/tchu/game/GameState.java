package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.lang.reflect.Array;
import java.util.*;

import static ch.epfl.tchu.game.Constants.INITIAL_CARDS_COUNT;//4

public class GameState  extends PublicGameState{

    private final Deck<Ticket> ticket;
    private final CardState cardState;
    private final Map<PlayerId, PlayerState> playerState;
    private  static final int NUMBER_OF_PLAYER = 2;

    private GameState(Deck<Ticket> ticket, CardState cardState, PlayerId currentPlayerId,
                      Map<PlayerId, PlayerState> playerState, PlayerId lastPlayer) {
        super(ticket.size(), cardState, currentPlayerId, Map.copyOf(playerState), lastPlayer);

         this.cardState = cardState;
         this.playerState = playerState;
         this.ticket = ticket;
    }


    /**
     *methode retournant l'état initial d'une partie dans laquelle la pioche des billets
     * contient : tickets , et la pioche des cartes contient les cartes,sauf les 8 premieres
     * distribuées aux joueurs
     *
     * lespioches sont mélangées ,
     * on choisi au hasard l'identité du premier joueur.
     */
    public static GameState initial(SortedBag<Ticket> tickets, Random rng){
        Map<PlayerId, PlayerState> playerState = new EnumMap<>(PlayerId.class);
        Deck<Ticket> ticketShuffled = Deck.of(tickets,rng);
        Deck<Card> ourDeck;
        ourDeck=Deck.of(Constants.ALL_CARDS, rng);

        playerState.put(PlayerId.PLAYER_1,
                PlayerState.initial(ourDeck.topCards(INITIAL_CARDS_COUNT)));
        ourDeck = ourDeck.withoutTopCards(INITIAL_CARDS_COUNT);
        playerState.put(PlayerId.PLAYER_2,
                PlayerState.initial(ourDeck.topCards(INITIAL_CARDS_COUNT)));
        ourDeck = ourDeck.withoutTopCards(INITIAL_CARDS_COUNT);

        return new GameState(ticketShuffled,
                CardState.of(ourDeck),
                PlayerId.ALL.get(rng.nextInt(NUMBER_OF_PLAYER)),
                playerState,
                null);

    }
    /**
     * methode qui retourne les count billets du sommet de la pioche.
     *
     */
    public SortedBag<Ticket> topTickets(int count){
        Preconditions.checkArgument(0 <= count && count <= ticket.size());
        return (ticket.topCards(count));
    }
    /**
     *
     * methode qui retourne un état identique au récepteur, mais sans les count billets du sommet de la pioche.
     */
     public GameState withoutTopTickets(int count){
        Preconditions.checkArgument(0 <= count && count <= ticket.size());
        return new GameState(ticket.withoutTopCards(count),
                cardState,
                currentPlayerId(),
                playerState,
                lastPlayer());
     }
    /**
     *  retourne la carte au sommet de la pioche
     *
     */
     public Card topCard(){
        Preconditions.checkArgument(!cardState.isDeckEmpty());
        return (cardState.topDeckCard());
     }
    /**
     *
     *retourne un état identique au récepteur mais sans la carte au sommet de la pioche
     *
     */
     public GameState withoutTopCard(){
         Preconditions.checkArgument(!cardState.isDeckEmpty());
         return new GameState(ticket,
                 cardState.withoutTopDeckCard(),
                 currentPlayerId(),
                 playerState,
                 lastPlayer() );

     }
    /**
     *
     *retourne un état identique au récepteur mais avec les cartes données ajoutées à la défausse
     *
     */
     public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards){
         return new GameState(ticket,
                 cardState.withMoreDiscardedCards(discardedCards),
                 currentPlayerId(),
                 playerState,
                 lastPlayer());
     }
    /**
     *retourne un état identique au récepteur sauf si la pioche de cartes est vide,
     *  dans ce cas elle est recréée(la pioche) à partir de la défausse
     *
     */
     public GameState withCardsDeckRecreatedIfNeeded(Random rng){
        //TODO aloulou ya miboun
         CardState newCardState = cardState;
         if(cardState.isDeckEmpty()){
             newCardState = cardState.withDeckRecreatedFromDiscards(rng);
         }
         return new GameState(ticket,
                    newCardState,
                    currentPlayerId(),
                    playerState,
                    lastPlayer());
     }

    /**
     * Retourne l'état complet du joueur d'identité donnée, et pas seulement sa partie publique
     */
    @Override
    public PlayerState playerState(PlayerId playerId){
        return playerState.get(playerId);
    }

    /**
     * Retourne l'état complet du joueur courant, et pas seulement sa partie publique
     */
    @Override
    public PlayerState currentPlayerState(){
        return playerState.get(currentPlayerId());
    }

    /**
     * Retourne un état identique au récepteur mais dans lequel
     * les billets donnés ont été ajoutés à la main du joueur donné
     */
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets){
        Preconditions.checkArgument(playerState.get(playerId).tickets().isEmpty());
        PlayerState newPlayerState = currentPlayerState().withAddedTickets(chosenTickets);
        Map<PlayerId, PlayerState> newMap = new HashMap<>(playerState);
        newMap.put(currentPlayerId(),newPlayerState);
        //TODO vérifier MAP
        return new GameState(ticket,
                cardState,
                currentPlayerId(),
                newMap,
                lastPlayer());
    }

    /**
     * Retourne un état identique au récepteur, mais dans lequel le joueur courant a tiré les billets drawnTickets
     * du sommet de la pioche, et choisi de garder ceux contenus dans chosenTicket
     */
    //TODO doit on retirer les cartes aussi dans carteState ?
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets){
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));
        PlayerState newPlayerState = currentPlayerState().withAddedTickets(chosenTickets);
        Map<PlayerId, PlayerState> newMap = new HashMap<>(playerState);
        newMap.put(currentPlayerId(),newPlayerState);
        //TODO a verifier MAP
        return (new GameState(ticket.withoutTopCards(drawnTickets.size()),
                cardState,
                currentPlayerId(),
                newMap,
                lastPlayer()));

    }

    /**
     * Retourne un état identique au récepteur si ce n'est que la carte face retournée à l'emplacement donné a été placée
     * dans la main du joueur courant, et remplacée par celle au sommet de la pioche
     */
    public GameState withDrawnFaceUpCard(int slot){
        Preconditions.checkArgument(canDrawCards());
        PlayerState newPlayerState = currentPlayerState().withAddedCard(cardState.faceUpCards().get(slot));
        CardState newCardState = cardState.withDrawnFaceUpCard(slot);
        Map<PlayerId, PlayerState> newMap = new HashMap<>(playerState);
        //TODO a vérifier MAP
        newMap.put(currentPlayerId(),newPlayerState);
        return new GameState(this.ticket,
                newCardState,
                currentPlayerId(),
                newMap,
                lastPlayer());
    }

    /**
     * Retourne un état identique au récepteur si ce n'est que la carte du sommet
     * de la pioche a été placée dans la main du joueur courant
     */
    public GameState withBlindlyDrawnCard(){
        Preconditions.checkArgument(canDrawCards());
        PlayerState newPlayerState = currentPlayerState().withAddedCard(cardState.topDeckCard());
        CardState newCardState = cardState.withoutTopDeckCard();
        Map<PlayerId, PlayerState> newMap = new HashMap<>(playerState);
        //TODO a vérifier MAP
        newMap.put(currentPlayerId(),newPlayerState);
        return new GameState(this.ticket,
                newCardState,
                currentPlayerId(),
                newMap,
                lastPlayer());
    }


    /**
     * Retourne un état identique au récepteur mais dans lequel le joueur
     * courant s'est emparé de la route donnée au moyen des cartes données
     * @param route route prise par le joueur
     * @param cards carte que l
     * @return le nouveau GameState
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards){
        List<Route> newRoutes = new ArrayList<>(currentPlayerState().routes());
        newRoutes.add(route);
        PlayerState newPlayerState = new PlayerState(
                currentPlayerState().tickets(),
                currentPlayerState().cards().difference(cards),
                newRoutes);
        Map<PlayerId, PlayerState> newMap = new HashMap<>(playerState);
        //TODO a vérifier MAP
        newMap.put(currentPlayerId(),newPlayerState);
        CardState newCardState = cardState.withMoreDiscardedCards(cards);
        return new GameState(this.ticket,
                newCardState,
                currentPlayerId(),
                newMap,
                lastPlayer());
    }

    /**
     * Retourne vrai si l'identité du dernier joueur est actuellement inconnue
     * et que le joueur courant n'a plus que deux wagons ou moins
     */

    /**
     *
     * @return
     */
    public boolean lastTurnBegins(){
        return ((lastPlayer()==null) && (playerState.get(currentPlayerId()).carCount() <= 2));
    }

    /**
     * termine le tour du joueur courant, c-à-d retourne un état identique au
     * récepteur si ce n'est que le joueur courant est celui qui suit le joueur
     * courant actuel; de plus, si lastTurnBegins retourne vrai, le joueur
     * courant actuel devient le dernier joueur
     */
    //TODO
    public GameState forNextTurn(){
       PlayerId lastPlayer = lastPlayer();
        if (lastTurnBegins() == true){
            lastPlayer = currentPlayerId();
        }
        return new GameState(ticket,
                cardState,
                currentPlayerId().next(),
                playerState,
                lastPlayer );
    }

}
