package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.Map;
import java.util.Random;

public class GameState  extends PublicGameState{


    private final Deck<Ticket> ticket;
    private final CardState cardState;
    private final Map<PlayerId, PlayerState> playerState;


    private GameState(Deck<Ticket> ticket, CardState cardState, PlayerId currentPlayerId,
                      Map<PlayerId, PlayerState> playerState, PlayerId lastPlayer) {
        super(ticket.size(), cardState, currentPlayerId, Map.copyOf(playerState), lastPlayer);

         this.cardState = cardState;
         this.playerState = playerState;
         this.ticket = ticket;
    }

    public static GameState initial(SortedBag<Ticket> tickets, Random rng){

        return new GameState();

    }



}
