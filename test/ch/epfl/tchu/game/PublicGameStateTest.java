package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.test.TestRandomizer;
import ch.epfl.tchu.game.PlayerId;

import java.util.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PublicGameStateTest {

    //--------------------------------------------------Constructeur----------------------------------------------
    @Test
    void PublicGameStateConstructorException1(){
        Map<PlayerId, PublicPlayerState> playerState= new EnumMap<>(PlayerId.class);
        playerState.put(PlayerId.PLAYER_1, new PublicPlayerState(0,0, List.of()));
        playerState.put(PlayerId.PLAYER_2, new PublicPlayerState(0,0, List.of()));

        assertThrows(IllegalArgumentException.class, () -> {
            new PublicGameState(1,
                    new PublicCardState(List.of(Card.BLUE,Card.ORANGE,Card.YELLOW,Card.LOCOMOTIVE,Card.LOCOMOTIVE)
                            ,-1,2), PlayerId.PLAYER_1, playerState, PlayerId.PLAYER_2);
        });

        new PublicGameState(1,
                new PublicCardState(List.of(Card.BLUE,Card.ORANGE,Card.YELLOW,Card.LOCOMOTIVE,Card.LOCOMOTIVE)
                        ,0,2), PlayerId.PLAYER_1, playerState, PlayerId.PLAYER_2);
    }

    @Test
    void PublicGameStateConstructorException2(){
        Map<PlayerId, PublicPlayerState> playerState= new EnumMap<>(PlayerId.class);
        playerState.put(PlayerId.PLAYER_1, new PublicPlayerState(0,0, List.of()));

        assertThrows(IllegalArgumentException.class, () -> {
            new PublicGameState(1,
                    new PublicCardState(List.of(Card.BLUE,Card.ORANGE,Card.YELLOW,Card.LOCOMOTIVE,Card.LOCOMOTIVE)
                            ,1,2), PlayerId.PLAYER_1, playerState, PlayerId.PLAYER_2);
        });
    }

    @Test
    void PublicGameStateConstructorException3(){
        Map<PlayerId, PublicPlayerState> playerState= new EnumMap<>(PlayerId.class);
        playerState.put(PlayerId.PLAYER_1, new PublicPlayerState(0,0, List.of()));
        playerState.put(PlayerId.PLAYER_2, new PublicPlayerState(0,0, List.of()));

        assertThrows(NullPointerException.class, () -> {
            new PublicGameState(1,
                    new PublicCardState(List.of(Card.BLUE,Card.ORANGE,Card.YELLOW,Card.LOCOMOTIVE,Card.LOCOMOTIVE)
                            ,1,2),
                    null, playerState, PlayerId.PLAYER_2);
        });

        assertThrows(NullPointerException.class, () -> {
            new PublicGameState(1,
                    new PublicCardState(List.of(Card.BLUE,Card.ORANGE,Card.YELLOW,Card.LOCOMOTIVE,Card.LOCOMOTIVE)
                            ,1,2),
                    PlayerId.PLAYER_1, null, PlayerId.PLAYER_2);
        });

        assertThrows(NullPointerException.class, () -> {
            new PublicGameState(1, null, PlayerId.PLAYER_1, playerState, PlayerId.PLAYER_2);
        });
    }

    //-------------------------------------------------ticketsCount()-------------------------------------------
    @Test
    void ticketsCountCheck1(){
        Map<PlayerId, PublicPlayerState> playerState= new EnumMap<>(PlayerId.class);
        playerState.put(PlayerId.PLAYER_1, new PublicPlayerState(0,0, List.of()));
        playerState.put(PlayerId.PLAYER_2, new PublicPlayerState(0,0, List.of()));

        PublicGameState gameState = new PublicGameState(1,
                new PublicCardState(List.of(Card.BLUE,Card.ORANGE,Card.YELLOW,Card.LOCOMOTIVE,Card.LOCOMOTIVE)
                        ,0,2), PlayerId.PLAYER_1, playerState, PlayerId.PLAYER_2);

        assertEquals(1, gameState.ticketsCount());
    }

    @Test
    void ticketsCountCheck2(){
        Map<PlayerId, PublicPlayerState> playerState= new EnumMap<>(PlayerId.class);
        playerState.put(PlayerId.PLAYER_1, new PublicPlayerState(0,0, List.of()));
        playerState.put(PlayerId.PLAYER_2, new PublicPlayerState(0,0, List.of()));

        PublicGameState gameState = new PublicGameState(5,
                new PublicCardState(List.of(Card.BLUE,Card.ORANGE,Card.YELLOW,Card.LOCOMOTIVE,Card.LOCOMOTIVE)
                        ,0,2), PlayerId.PLAYER_1, playerState, PlayerId.PLAYER_2);

        assertEquals(5, gameState.ticketsCount());
    }

    //-----------------------------------------------canDrawTickets()-----------------------------------------------
    @Test
    void canDrawTicketsCheckTrue(){
        Map<PlayerId, PublicPlayerState> playerState= new EnumMap<>(PlayerId.class);
        playerState.put(PlayerId.PLAYER_1, new PublicPlayerState(0,0, List.of()));
        playerState.put(PlayerId.PLAYER_2, new PublicPlayerState(0,0, List.of()));

        PublicGameState gameState = new PublicGameState(5,
                new PublicCardState(List.of(Card.BLUE,Card.ORANGE,Card.YELLOW,Card.LOCOMOTIVE,Card.LOCOMOTIVE)
                        ,0,2), PlayerId.PLAYER_1, playerState, PlayerId.PLAYER_2);

        assertTrue(gameState.canDrawTickets());
    }

    @Test
    void canDrawTicketsCheckFalse(){
        Map<PlayerId, PublicPlayerState> playerState= new EnumMap<>(PlayerId.class);
        playerState.put(PlayerId.PLAYER_1, new PublicPlayerState(0,0, List.of()));
        playerState.put(PlayerId.PLAYER_2, new PublicPlayerState(0,0, List.of()));

        PublicGameState gameState = new PublicGameState(0,
                new PublicCardState(List.of(Card.BLUE,Card.ORANGE,Card.YELLOW,Card.LOCOMOTIVE,Card.LOCOMOTIVE)
                        ,0,2), PlayerId.PLAYER_1, playerState, PlayerId.PLAYER_2);

        assertFalse(gameState.canDrawTickets());
    }

    //------------------------------------------------cardState()-------------------------------------------------
    @Test
    void cardStateCheck(){
        Map<PlayerId, PublicPlayerState> playerState= new EnumMap<>(PlayerId.class);
        playerState.put(PlayerId.PLAYER_1, new PublicPlayerState(0,0, List.of()));
        playerState.put(PlayerId.PLAYER_2, new PublicPlayerState(0,0, List.of()));

        PublicCardState cardState =  new PublicCardState(
                List.of(Card.BLUE,Card.ORANGE,Card.YELLOW,Card.LOCOMOTIVE,Card.LOCOMOTIVE),0,2
        );

        PublicGameState gameState = new PublicGameState(0, cardState, PlayerId.PLAYER_1, playerState, PlayerId.PLAYER_2);

        assertEquals(cardState, gameState.cardState());
    }

    //-----------------------------------------------canDrawCards()-----------------------------------------------
    @Test
    void canDrawCardsCheckTrue1(){
        Map<PlayerId, PublicPlayerState> playerState= new EnumMap<>(PlayerId.class);
        playerState.put(PlayerId.PLAYER_1, new PublicPlayerState(0,0, List.of()));
        playerState.put(PlayerId.PLAYER_2, new PublicPlayerState(0,0, List.of()));

        PublicGameState gameState = new PublicGameState(5,
                new PublicCardState(List.of(Card.BLUE,Card.ORANGE,Card.YELLOW,Card.LOCOMOTIVE,Card.LOCOMOTIVE)
                        ,5,0), PlayerId.PLAYER_1, playerState, PlayerId.PLAYER_2);

        assertTrue(gameState.canDrawCards());
    }

    @Test
    void canDrawCardsCheckTrue2(){
        Map<PlayerId, PublicPlayerState> playerState= new EnumMap<>(PlayerId.class);
        playerState.put(PlayerId.PLAYER_1, new PublicPlayerState(0,0, List.of()));
        playerState.put(PlayerId.PLAYER_2, new PublicPlayerState(0,0, List.of()));

        PublicGameState gameState = new PublicGameState(5,
                new PublicCardState(List.of(Card.BLUE,Card.ORANGE,Card.YELLOW,Card.LOCOMOTIVE,Card.LOCOMOTIVE)
                        ,0,5), PlayerId.PLAYER_1, playerState, PlayerId.PLAYER_2);

        assertTrue(gameState.canDrawCards());
    }

    @Test
    void canDrawCardsCheckTrue3(){
        Map<PlayerId, PublicPlayerState> playerState= new EnumMap<>(PlayerId.class);
        playerState.put(PlayerId.PLAYER_1, new PublicPlayerState(0,0, List.of()));
        playerState.put(PlayerId.PLAYER_2, new PublicPlayerState(0,0, List.of()));

        PublicGameState gameState = new PublicGameState(5,
                new PublicCardState(List.of(Card.BLUE,Card.ORANGE,Card.YELLOW,Card.LOCOMOTIVE,Card.LOCOMOTIVE)
                        ,2,3), PlayerId.PLAYER_1, playerState, PlayerId.PLAYER_2);

        assertTrue(gameState.canDrawCards());
    }

    @Test
    void canDrawCardsCheckFalse(){
        Map<PlayerId, PublicPlayerState> playerState= new EnumMap<>(PlayerId.class);
        playerState.put(PlayerId.PLAYER_1, new PublicPlayerState(0,0, List.of()));
        playerState.put(PlayerId.PLAYER_2, new PublicPlayerState(0,0, List.of()));

        PublicGameState gameState = new PublicGameState(5,
                new PublicCardState(List.of(Card.BLUE,Card.ORANGE,Card.YELLOW,Card.LOCOMOTIVE,Card.LOCOMOTIVE)
                        ,2,2), PlayerId.PLAYER_1, playerState, PlayerId.PLAYER_2);

        assertFalse(gameState.canDrawCards());
    }

    //---------------------------------------------currentPlayerId()----------------------------------------
    @Test
    void currentPlayerIdCheck(){
        Map<PlayerId, PublicPlayerState> playerState= new EnumMap<>(PlayerId.class);
        playerState.put(PlayerId.PLAYER_1, new PublicPlayerState(0,0, List.of()));
        playerState.put(PlayerId.PLAYER_2, new PublicPlayerState(0,0, List.of()));

        PublicGameState gameState = new PublicGameState(5,
                new PublicCardState(List.of(Card.BLUE,Card.ORANGE,Card.YELLOW,Card.LOCOMOTIVE,Card.LOCOMOTIVE)
                        ,3,2), PlayerId.PLAYER_1, playerState, PlayerId.PLAYER_2);

        assertEquals(PlayerId.PLAYER_1, gameState.currentPlayerId());
    }

    //---------------------------------------------playerState()----------------------------------------
    @Test
    void playerStateCheck(){
        PublicPlayerState player1State = new PublicPlayerState(1,0, List.of());
        PublicPlayerState player2State = new PublicPlayerState(0,1, List.of());

        Map<PlayerId, PublicPlayerState> playerState= new EnumMap<>(PlayerId.class);
        playerState.put(PlayerId.PLAYER_1, player1State);
        playerState.put(PlayerId.PLAYER_2, player2State);

        PublicGameState gameState = new PublicGameState(5,
                new PublicCardState(List.of(Card.BLUE,Card.ORANGE,Card.YELLOW,Card.LOCOMOTIVE,Card.LOCOMOTIVE)
                        ,3,2), PlayerId.PLAYER_1, playerState, PlayerId.PLAYER_2);

        assertEquals(player1State, gameState.playerState(PlayerId.PLAYER_1));
        assertEquals(player2State, gameState.playerState(PlayerId.PLAYER_2));
    }

    //---------------------------------------------currentPlayerState()----------------------------------------
    @Test
    void currentPlayerStateCheck1(){
        PublicPlayerState player1State = new PublicPlayerState(1,0, List.of());
        PublicPlayerState player2State = new PublicPlayerState(0,1, List.of());

        Map<PlayerId, PublicPlayerState> playerState= new EnumMap<>(PlayerId.class);
        playerState.put(PlayerId.PLAYER_1, player1State);
        playerState.put(PlayerId.PLAYER_2, player2State);

        PublicGameState gameState = new PublicGameState(5,
                new PublicCardState(List.of(Card.BLUE,Card.ORANGE,Card.YELLOW,Card.LOCOMOTIVE,Card.LOCOMOTIVE)
                        ,3,2), PlayerId.PLAYER_2, playerState, null);

        assertEquals(player2State, gameState.currentPlayerState());
    }

    @Test
    void currentPlayerStateCheck2(){
        PublicPlayerState player1State = new PublicPlayerState(1,0, List.of());
        PublicPlayerState player2State = new PublicPlayerState(0,1, List.of());

        Map<PlayerId, PublicPlayerState> playerState= new EnumMap<>(PlayerId.class);
        playerState.put(PlayerId.PLAYER_1, player1State);
        playerState.put(PlayerId.PLAYER_2, player2State);

        PublicGameState gameState = new PublicGameState(5,
                new PublicCardState(List.of(Card.BLUE,Card.ORANGE,Card.YELLOW,Card.LOCOMOTIVE,Card.LOCOMOTIVE)
                        ,3,2), PlayerId.PLAYER_1, playerState, null);

        assertEquals(player1State, gameState.currentPlayerState());
    }

    //-----------------------------------------------claimedRoutes()------------------------------------------
    @Test
    void claimedRoutesCheck(){
        PublicPlayerState player1State = new PublicPlayerState(1,0, List.of(
                ChMap.routes().get(0), ChMap.routes().get(1), ChMap.routes().get(2)
        ));
        PublicPlayerState player2State = new PublicPlayerState(0,1, List.of(
                ChMap.routes().get(3), ChMap.routes().get(4), ChMap.routes().get(5)
        ));

        Map<PlayerId, PublicPlayerState> playerState= new EnumMap<>(PlayerId.class);
        playerState.put(PlayerId.PLAYER_1, player1State);
        playerState.put(PlayerId.PLAYER_2, player2State);

        PublicGameState gameState = new PublicGameState(5,
                new PublicCardState(List.of(Card.BLUE,Card.ORANGE,Card.YELLOW,Card.LOCOMOTIVE,Card.LOCOMOTIVE)
                        ,3,2), PlayerId.PLAYER_1, playerState, null);

        assertEquals(List.of(ChMap.routes().get(0), ChMap.routes().get(1), ChMap.routes().get(2), ChMap.routes().get(3), ChMap.routes().get(4), ChMap.routes().get(5)),
                gameState.claimedRoutes());
    }

    //----------------------------------------------lastPlayer()----------------------------------------------
    @Test
    void lastPlayerCheck1(){
        PublicPlayerState player1State = new PublicPlayerState(1,0, List.of());
        PublicPlayerState player2State = new PublicPlayerState(0,1, List.of());

        Map<PlayerId, PublicPlayerState> playerState= new EnumMap<>(PlayerId.class);
        playerState.put(PlayerId.PLAYER_1, player1State);
        playerState.put(PlayerId.PLAYER_2, player2State);

        PublicGameState gameState = new PublicGameState(5,
                new PublicCardState(List.of(Card.BLUE,Card.ORANGE,Card.YELLOW,Card.LOCOMOTIVE,Card.LOCOMOTIVE)
                        ,3,2), PlayerId.PLAYER_1, playerState, PlayerId.PLAYER_2);

        assertEquals(PlayerId.PLAYER_2, gameState.lastPlayer());
    }

    @Test
    void lastPlayerCheck2(){
        PublicPlayerState player1State = new PublicPlayerState(1,0, List.of());
        PublicPlayerState player2State = new PublicPlayerState(0,1, List.of());

        Map<PlayerId, PublicPlayerState> playerState= new EnumMap<>(PlayerId.class);
        playerState.put(PlayerId.PLAYER_1, player1State);
        playerState.put(PlayerId.PLAYER_2, player2State);

        PublicGameState gameState = new PublicGameState(5,
                new PublicCardState(List.of(Card.BLUE,Card.ORANGE,Card.YELLOW,Card.LOCOMOTIVE,Card.LOCOMOTIVE)
                        ,3,2), PlayerId.PLAYER_1, playerState, null);

        assertEquals(null, gameState.lastPlayer());
    }
}
