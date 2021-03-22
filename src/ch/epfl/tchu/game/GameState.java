package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.Map;
import java.util.Random;

public class GameState  extends PublicGameState{

    private final int ticketsCount;
    private final PublicCardState cardState;
    private final PlayerId currentPlayerId;
    private final Map<PlayerId, PlayerState> playerState;
    private final PlayerId lastPlayer;

    private GameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId,
                      Map<PlayerId, PlayerState> playerState, PlayerId lastPlayer) {
        super(ticketsCount, cardState, currentPlayerId, playerState, lastPlayer);
         this.ticketsCount = ticketsCount;
         this.PublicCardState cardState = cardState;
         this.PlayerId currentPlayerId = currentPlayerId;
         this.Map<PlayerId, PlayerState> playerState = playerState;
         this.PlayerId lastPlayer = lastPlayer;
        
    }
}
