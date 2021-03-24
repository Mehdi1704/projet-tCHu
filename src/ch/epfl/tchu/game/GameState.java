package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

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

    public static GameState initial(SortedBag<Ticket> tickets, Random rng){
        Map<PlayerId, PlayerState> playerState = new EnumMap<>(PlayerId.class);
        Deck<Ticket> ticketShuffled = Deck.of(tickets,rng);
        Deck<Card> ourDeck;
        ourDeck=Deck.of(Constants.ALL_CARDS, rng);

            playerState.put(PlayerId.PLAYER_1,PlayerState.initial(ourDeck.topCards(INITIAL_CARDS_COUNT)));
            ourDeck = ourDeck.withoutTopCards(INITIAL_CARDS_COUNT);
            playerState.put(PlayerId.PLAYER_2,PlayerState.initial(ourDeck.topCards(INITIAL_CARDS_COUNT)));
            ourDeck = ourDeck.withoutTopCards(INITIAL_CARDS_COUNT);

        return new GameState(ticketShuffled,CardState.of(ourDeck),PlayerId.ALL.get(rng.nextInt(NUMBER_OF_PLAYER)),
                playerState, null);

    }

    public SortedBag<Ticket> topTickets(int count){
        Preconditions.checkArgument(count>= 0 && count <= ticket.size());
        return (ticket.topCards(count));
    }



     public GameState withoutTopTickets(int count){
        Preconditions.checkArgument(count>= 0 && count <= ticket.size());
        return new GameState( ticket.withoutTopCards(count), cardState, currentPlayerId(), playerState, lastPlayer() );
     }

     public Card topCard(){
        Preconditions.checkArgument(!cardState.isDeckEmpty());
        return (cardState.topDeckCard());
     }


     public GameState withoutTopCard(){
         Preconditions.checkArgument(!cardState.isDeckEmpty());
         return new GameState( ticket, cardState.withoutTopDeckCard(), currentPlayerId(), playerState, lastPlayer() );

     }

     public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards){
         return new GameState( ticket, cardState.withMoreDiscardedCards(discardedCards), currentPlayerId(), playerState,
                 lastPlayer() );
     }

     public GameState withCardsDeckRecreatedIfNeeded(Random rng){
        if(cardState.isDeckEmpty()){
            return new GameState( ticket, cardState.withDeckRecreatedFromDiscards(rng), currentPlayerId(), playerState,
                    lastPlayer() );
        }else{
            return new GameState( ticket, cardState, currentPlayerId(), playerState,lastPlayer() );
        }

     }

     //TODO doit on retirer les cartes aussi dans carteState ?

    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets){
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));
       // PlayerState newPlayerState= (PlayerState) currentPlayerState();
       // newPlayerState.withAddedTickets(chosenTickets);
    //TODO a changer
        return (new GameState(ticket.withoutTopCards(drawnTickets.size()), cardState, currentPlayerId(),
                playerState,lastPlayer()));

    }

}
