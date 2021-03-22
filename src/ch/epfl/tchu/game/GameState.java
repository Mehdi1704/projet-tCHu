package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.Map;
import java.util.Random;

public class GameState  extends PublicGameState{

    private final int ticketsCount;

    private GameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId,
                      Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer) {
        super(ticketsCount, cardState, currentPlayerId, playerState, lastPlayer);





    }
}
