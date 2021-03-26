package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GameStateTest {

    //-----------------------------------------------initial()---------------------------------------------
    @Test
    void initialCheckNonRandom(){
        SortedBag<Ticket> tickets = SortedBag.of(List.of(
            ChMap.tickets().get(0), ChMap.tickets().get(1)
        ));
        GameState gamestate = GameState.initial(tickets, TestRandomizer.NON_RANDOM);

        assertEquals(SortedBag.of(4, Card.LOCOMOTIVE), gamestate.playerState(PlayerId.PLAYER_1).cards());
        assertEquals(SortedBag.of(4, Card.LOCOMOTIVE), gamestate.playerState(PlayerId.PLAYER_2).cards());

        assertEquals(PlayerId.PLAYER_2, gamestate.currentPlayerId());
        assertEquals(null, gamestate.lastPlayer());
        assertEquals(tickets, gamestate.topTickets(tickets.size()));
        assertEquals(Constants.ALL_CARDS.size()-8-5, gamestate.cardState().deckSize());
        assertEquals(5, gamestate.cardState().faceUpCards().size());
        assertEquals(0, gamestate.cardState().discardsSize());
    }

    @Test
    void initialCheckRandom(){
        SortedBag<Ticket> tickets = SortedBag.of(List.of(
                ChMap.tickets().get(0), ChMap.tickets().get(1)
        ));
        GameState gamestate = GameState.initial(tickets, TestRandomizer.newRandom());

        assertEquals(SortedBag.of(List.of(Card.VIOLET, Card.BLUE, Card.WHITE, Card.LOCOMOTIVE)),
                gamestate.playerState(PlayerId.PLAYER_1).cards());

        assertEquals(SortedBag.of(List.of(Card.BLACK, Card.BLACK, Card.VIOLET, Card.LOCOMOTIVE)),
                gamestate.playerState(PlayerId.PLAYER_2).cards());

        assertEquals( PlayerId.ALL.get(TestRandomizer.newRandom().nextInt(2)), gamestate.currentPlayerId());
        assertEquals(null, gamestate.lastPlayer());
        assertEquals(tickets, gamestate.topTickets(tickets.size()));
        assertEquals(Constants.ALL_CARDS.size()-8-5, gamestate.cardState().deckSize());
        assertEquals(5, gamestate.cardState().faceUpCards().size());
        assertEquals(0, gamestate.cardState().discardsSize());
    }

    //---------------------------------------------currentPlayerId()----------------------------------------
    @Test
    void currentPlayerIdCheck(){
        SortedBag<Ticket> tickets = SortedBag.of(List.of(
                ChMap.tickets().get(0), ChMap.tickets().get(1)
        ));

        GameState gameState = GameState.initial(tickets, TestRandomizer.newRandom());
        PlayerState playerState = new PlayerState(SortedBag.of(), gameState.playerState(gameState.currentPlayerId()).cards(), List.of());

        assertEquals(playerState.cards(), gameState.currentPlayerState().cards());
        assertEquals(playerState.tickets(), gameState.currentPlayerState().tickets());
        assertEquals(playerState.routes(), gameState.currentPlayerState().routes());
        assertEquals(0, gameState.currentPlayerState().tickets().size());
    }

    //---------------------------------------------playerState()----------------------------------------
    @Test
    void playerStateCheck(){
        SortedBag<Ticket> tickets = SortedBag.of(List.of(
                ChMap.tickets().get(0), ChMap.tickets().get(1)
        ));

        GameState gameState = GameState.initial(tickets, TestRandomizer.newRandom());
        PlayerState playerState1 = new PlayerState(SortedBag.of(), gameState.playerState(PlayerId.PLAYER_1).cards(), List.of());
        PlayerState playerState2 = new PlayerState(SortedBag.of(), gameState.playerState(PlayerId.PLAYER_2).cards(), List.of());

        assertEquals(playerState1.cards(), gameState.playerState(PlayerId.PLAYER_1).cards());
        assertEquals(playerState1.tickets(), gameState.playerState(PlayerId.PLAYER_1).tickets());
        assertEquals(playerState1.routes(), gameState.playerState(PlayerId.PLAYER_1).routes());
        assertEquals(0, gameState.playerState(PlayerId.PLAYER_1).tickets().size());

        assertEquals(playerState2.cards(), gameState.playerState(PlayerId.PLAYER_2).cards());
        assertEquals(playerState2.tickets(), gameState.playerState(PlayerId.PLAYER_2).tickets());
        assertEquals(playerState2.routes(), gameState.playerState(PlayerId.PLAYER_2).routes());
        assertEquals(0, gameState.playerState(PlayerId.PLAYER_2).tickets().size());
    }

    //-------------------------------------------topTickets()-------------------------------------------------
    @Test
    void topTicketsException(){
        SortedBag<Ticket> tickets = SortedBag.of(List.of(
                ChMap.tickets().get(0), ChMap.tickets().get(1)
        ));

        GameState gameState = GameState.initial(tickets, TestRandomizer.newRandom());

        assertThrows(IllegalArgumentException.class, () -> {
           gameState.topTickets(3);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            gameState.topTickets(-1);
        });

        gameState.topTickets(0);
        gameState.topTickets(2);
    }

    @Test
    void topTicketsCheck(){
        SortedBag<Ticket> tickets = SortedBag.of(List.of(
                ChMap.tickets().get(0), ChMap.tickets().get(1)
        ));

        GameState gameState = GameState.initial(tickets, TestRandomizer.NON_RANDOM);
        assertEquals(tickets, gameState.topTickets(2));
        assertEquals(SortedBag.of(ChMap.tickets().get(1)), gameState.topTickets(2).get(1));
        assertEquals(SortedBag.of(), gameState.topTickets(0));
    }

    //-------------------------------------------withoutTopTickets()-------------------------------------------------
    @Test
    void withoutTopTicketsException(){
        SortedBag<Ticket> tickets = SortedBag.of(List.of(
                ChMap.tickets().get(0), ChMap.tickets().get(1)
        ));

        GameState gameState = GameState.initial(tickets, TestRandomizer.newRandom());

        assertThrows(IllegalArgumentException.class, () -> {
            gameState.withoutTopTickets(3);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            gameState.withoutTopTickets(-1);
        });

        gameState.withoutTopTickets(0);
        gameState.withoutTopTickets(2);
    }

    @Test
    void withoutTopTicketsCheck(){
        SortedBag<Ticket> tickets = SortedBag.of(List.of(
                ChMap.tickets().get(0), ChMap.tickets().get(1)
        ));

        GameState gameState = GameState.initial(tickets, TestRandomizer.NON_RANDOM);
        assertEquals(0, gameState.withoutTopTickets(2).ticketsCount());
        assertEquals(1, gameState.withoutTopTickets(1).ticketsCount());
        assertEquals(tickets.size(), gameState.withoutTopTickets(0).ticketsCount());
    }

    //--------------------------------------------topCard()-------------------------------------------
    @Test
    void topCardException(){
        SortedBag<Ticket> tickets = SortedBag.of(List.of(
                ChMap.tickets().get(0), ChMap.tickets().get(1)
        ));

        GameState gameState = GameState.initial(tickets, TestRandomizer.newRandom());

        for(int i=0 ; i<Constants.ALL_CARDS.size()-8-5; i++){
            gameState=gameState.withoutTopCard();
        }

        final GameState finalGameState = gameState;
        assertThrows(IllegalArgumentException.class, () -> {
            finalGameState.topCard();
        });
    }

    @Test
    void topCardCheck(){
        SortedBag<Ticket> tickets = SortedBag.of(List.of(
                ChMap.tickets().get(0), ChMap.tickets().get(1)
        ));

        GameState gameState = GameState.initial(tickets, TestRandomizer.newRandom());
        assertEquals(Deck.of(Constants.ALL_CARDS, TestRandomizer.newRandom()).withoutTopCards(8+5).topCard(),
                gameState.topCard());
    }

    //--------------------------------------------topCard()-------------------------------------------
    @Test
    void withoutTopCardException(){
        SortedBag<Ticket> tickets = SortedBag.of(List.of(
                ChMap.tickets().get(0), ChMap.tickets().get(1)
        ));

        GameState gameState = GameState.initial(tickets, TestRandomizer.newRandom());

        for(int i=0 ; i<Constants.ALL_CARDS.size()-8-5; i++){
            gameState=gameState.withoutTopCard();
        }

        final GameState finalGameState = gameState;
        assertThrows(IllegalArgumentException.class, () -> {
            finalGameState.withoutTopCard();
        });
    }

    @Test
    void withoutTopCardCheck(){
        SortedBag<Ticket> tickets = SortedBag.of(List.of(
                ChMap.tickets().get(0), ChMap.tickets().get(1)
        ));

        GameState gameState = GameState.initial(tickets, TestRandomizer.newRandom());
        Deck<Card> deck = Deck.of(Constants.ALL_CARDS, TestRandomizer.newRandom()).withoutTopCards(8).withoutTopCards(5);
        int lenght = deck.size();

        for(int i=0 ; i<lenght-1; i++){
            gameState=gameState.withoutTopCard();
            deck = deck.withoutTopCard();
            assertEquals(deck.topCard(), gameState.topCard());
        }
        assertEquals(1, deck.size());
        assertFalse(gameState.canDrawCards());
    }

    //------------------------------------------------withMoreDiscardedCards()--------------------------------
    @Test
    void withMoreDiscardedCardsCheck(){
        SortedBag<Ticket> tickets = SortedBag.of(List.of(
                ChMap.tickets().get(0), ChMap.tickets().get(1)
        ));

        GameState gameState = GameState.initial(tickets, TestRandomizer.newRandom());
        SortedBag<Card> bagOfCards = SortedBag.of(2, Card.BLACK, 2, Card.BLUE);

        assertEquals(0, gameState.cardState().discardsSize());
        gameState = gameState.withMoreDiscardedCards(bagOfCards);
        assertEquals(bagOfCards.size(), gameState.cardState().discardsSize());
    }

    //------------------------------------------------withCardsDeckRecreatedIfNeeded-----------------------------
    @Test
    void withCardsDeckRecreatedIfNeededCheck1(){
        SortedBag<Ticket> tickets = SortedBag.of(List.of(
                ChMap.tickets().get(0), ChMap.tickets().get(1)
        ));

        GameState gameState = GameState.initial(tickets, TestRandomizer.newRandom());
        GameState discardedGameState = gameState.withCardsDeckRecreatedIfNeeded(TestRandomizer.NON_RANDOM);
        assertEquals(gameState, discardedGameState);
    }

    @Test
    void withCardsDeckRecreatedIfNeededCheck2(){
        SortedBag<Ticket> tickets = SortedBag.of(List.of(
                ChMap.tickets().get(0), ChMap.tickets().get(1)
        ));

        GameState gameState = GameState.initial(tickets, TestRandomizer.newRandom());

        int lenght = gameState.cardState().deckSize();
        for(int i=0 ; i<lenght; i++){
            gameState=gameState.withoutTopCard();
        }
        assertEquals(0, gameState.cardState().deckSize());

        SortedBag<Card> bagOfCards = SortedBag.of(2, Card.BLACK, 2, Card.BLUE);
        gameState = gameState.withMoreDiscardedCards(bagOfCards);
        gameState = gameState.withCardsDeckRecreatedIfNeeded(TestRandomizer.NON_RANDOM);

        assertEquals(bagOfCards.size(), gameState.cardState().deckSize());
    }

    //-------------------------------------------withInitiallyChosenTickets()-----------------------------------
    @Test
    void withInitiallyChosenTicketsException(){
        SortedBag<Ticket> tickets = SortedBag.of();
        SortedBag<Ticket> chosenTickets = SortedBag.of(List.of(
                ChMap.tickets().get(0), ChMap.tickets().get(1)
        ));

        GameState gameState = GameState.initial(tickets, TestRandomizer.newRandom());
        assertEquals(SortedBag.of(), gameState.playerState(PlayerId.PLAYER_1).tickets());
        assertEquals(SortedBag.of(), gameState.playerState(PlayerId.PLAYER_2).tickets());

        gameState = gameState.withInitiallyChosenTickets(PlayerId.PLAYER_1, chosenTickets);
        assertEquals(chosenTickets, gameState.playerState(PlayerId.PLAYER_1).tickets());
        assertEquals(SortedBag.of(), gameState.playerState(PlayerId.PLAYER_2).tickets());

        final GameState finalGameState = gameState;
        assertThrows(IllegalArgumentException.class, () -> {
            finalGameState.withInitiallyChosenTickets(PlayerId.PLAYER_1, chosenTickets);
        });
    }

    @Test
    void withInitiallyChosenTicketsCheck(){
        SortedBag<Ticket> tickets = SortedBag.of();
        SortedBag<Ticket> chosenTickets = SortedBag.of(List.of(
                ChMap.tickets().get(0), ChMap.tickets().get(1)
        ));

        GameState gameState = GameState.initial(tickets, TestRandomizer.newRandom());
        assertEquals(SortedBag.of(), gameState.playerState(PlayerId.PLAYER_1).tickets());
        assertEquals(SortedBag.of(), gameState.playerState(PlayerId.PLAYER_2).tickets());
        assertEquals(0, gameState.ticketsCount());

        gameState = gameState.withInitiallyChosenTickets(PlayerId.PLAYER_1, chosenTickets);
        assertEquals(chosenTickets, gameState.playerState(PlayerId.PLAYER_1).tickets());
        assertEquals(SortedBag.of(), gameState.playerState(PlayerId.PLAYER_2).tickets());
        assertEquals(0, gameState.ticketsCount());
    }

    //----------------------------------------withChosenAdditionalTickets()---------------------------------------
    @Test
    void withChosenAdditionalTicketsException(){
        SortedBag<Ticket> tickets = SortedBag.of(List.of(
                ChMap.tickets().get(0), ChMap.tickets().get(1), ChMap.tickets().get(2), ChMap.tickets().get(3)
        ));
        SortedBag<Ticket> chosenTickets = SortedBag.of(List.of(
                ChMap.tickets().get(0), ChMap.tickets().get(1), ChMap.tickets().get(2)
        ));
        SortedBag<Ticket> drawnTickets = SortedBag.of(List.of(
                ChMap.tickets().get(0), ChMap.tickets().get(1)
        ));

        assertFalse(drawnTickets.contains(chosenTickets));

        GameState gameState = GameState.initial(tickets, TestRandomizer.newRandom());

        final GameState finalGameState = gameState;
        assertThrows(IllegalArgumentException.class, () -> {
            finalGameState.withChosenAdditionalTickets(drawnTickets, chosenTickets);
        });
    }

    @Test
    void withChosenAdditionalTicketsCheck(){
        SortedBag<Ticket> tickets = SortedBag.of(List.of(
                ChMap.tickets().get(0), ChMap.tickets().get(1), ChMap.tickets().get(2), ChMap.tickets().get(3)
        ));
        SortedBag<Ticket> drawnTickets = SortedBag.of(List.of(
                ChMap.tickets().get(0), ChMap.tickets().get(1), ChMap.tickets().get(2)
        ));
        SortedBag<Ticket> chosenTickets = SortedBag.of(List.of(
                ChMap.tickets().get(0), ChMap.tickets().get(1)
        ));


        GameState gameState = GameState.initial(tickets, TestRandomizer.newRandom());
        assertEquals(4, gameState.ticketsCount());
        assertEquals(SortedBag.of(), gameState.playerState(gameState.currentPlayerId()).tickets());

        assertTrue(drawnTickets.contains(chosenTickets));

        gameState = gameState.withChosenAdditionalTickets(drawnTickets, chosenTickets);
        assertEquals(1, gameState.ticketsCount());
        assertEquals(chosenTickets, gameState.playerState(gameState.currentPlayerId()).tickets());
    }

    //--------------------------------------------withDrawnFaceUpCard()------------------------------------------
    @Test
    void withDrawnFaceUpCardException1(){
        SortedBag<Ticket> tickets = SortedBag.of(List.of(
                ChMap.tickets().get(0), ChMap.tickets().get(1), ChMap.tickets().get(2), ChMap.tickets().get(3)
        ));

        GameState gameState = GameState.initial(tickets, TestRandomizer.newRandom());

        for(int i=0 ; i<Constants.ALL_CARDS.size()-8-5; i++){
            gameState=gameState.withoutTopCard();
        }

        assertFalse(gameState.canDrawCards());

        final GameState finalGameState = gameState;
        assertThrows(IllegalArgumentException.class, () -> {
            finalGameState.withDrawnFaceUpCard(3);
        });
    }

    @Test
    void withDrawnFaceUpCardException2(){
        SortedBag<Ticket> tickets = SortedBag.of(List.of(
                ChMap.tickets().get(0), ChMap.tickets().get(1), ChMap.tickets().get(2), ChMap.tickets().get(3)
        ));

        GameState gameState = GameState.initial(tickets, TestRandomizer.newRandom());

        for(int i=0 ; i<Constants.ALL_CARDS.size()-8-5; i++){
            gameState=gameState.withoutTopCard();
        }

        SortedBag<Card> bagOfCards = SortedBag.of(3, Card.BLACK, 2, Card.BLUE);
        gameState = gameState.withMoreDiscardedCards(bagOfCards);
        assertTrue(gameState.canDrawCards());

        final GameState finalGameState = gameState;
        assertThrows(IllegalArgumentException.class, () -> {
            finalGameState.withDrawnFaceUpCard(3);
        });
    }

    @Test
    void withDrawnFaceUpCardCheck(){
        SortedBag<Ticket> tickets = SortedBag.of(List.of(
                ChMap.tickets().get(0), ChMap.tickets().get(1), ChMap.tickets().get(2), ChMap.tickets().get(3)
        ));

        GameState gameState = GameState.initial(tickets, TestRandomizer.newRandom());

        assertEquals(Card.YELLOW, gameState.cardState().faceUpCard(3));
        assertEquals(5, gameState.cardState().faceUpCards().size());
        assertEquals(Card.GREEN, gameState.topCard());
        assertEquals(SortedBag.of(List.of(Card.BLACK, Card.BLACK, Card.VIOLET, Card.LOCOMOTIVE)),
                gameState.currentPlayerState().cards());
        assertEquals(97, gameState.cardState().deckSize());

        gameState = gameState.withDrawnFaceUpCard(3);

        assertEquals(Card.GREEN, gameState.cardState().faceUpCard(3));
        assertEquals(5, gameState.cardState().faceUpCards().size());
        assertEquals(SortedBag.of(List.of(Card.BLACK, Card.BLACK, Card.VIOLET, Card.LOCOMOTIVE, Card.YELLOW)),
                gameState.currentPlayerState().cards());
        assertEquals(96, gameState.cardState().deckSize());
    }

    //--------------------------------------------withBlindlyDrawnCard()--------------------------------------
    @Test
    void withBlindlyDrawnCardException1(){
        SortedBag<Ticket> tickets = SortedBag.of(List.of(
                ChMap.tickets().get(0), ChMap.tickets().get(1), ChMap.tickets().get(2), ChMap.tickets().get(3)
        ));

        GameState gameState = GameState.initial(tickets, TestRandomizer.newRandom());

        for(int i=0 ; i<Constants.ALL_CARDS.size()-8-5; i++){
            gameState=gameState.withoutTopCard();
        }

        assertFalse(gameState.canDrawCards());

        final GameState finalGameState = gameState;
        assertThrows(IllegalArgumentException.class, () -> {
            finalGameState.withBlindlyDrawnCard();
        });
    }

    @Test
    void withBlindlyDrawnCardException2(){
        SortedBag<Ticket> tickets = SortedBag.of(List.of(
                ChMap.tickets().get(0), ChMap.tickets().get(1), ChMap.tickets().get(2), ChMap.tickets().get(3)
        ));

        GameState gameState = GameState.initial(tickets, TestRandomizer.newRandom());

        for(int i=0 ; i<Constants.ALL_CARDS.size()-8-5; i++){
            gameState=gameState.withoutTopCard();
        }

        SortedBag<Card> bagOfCards = SortedBag.of(3, Card.BLACK, 2, Card.BLUE);
        gameState = gameState.withMoreDiscardedCards(bagOfCards);
        assertTrue(gameState.canDrawCards());

        final GameState finalGameState = gameState;
        assertThrows(IllegalArgumentException.class, () -> {
            finalGameState.withBlindlyDrawnCard();
        });
    }

    @Test
    void withBlindlyDrawnCardCheck(){
        SortedBag<Ticket> tickets = SortedBag.of(List.of(
                ChMap.tickets().get(0), ChMap.tickets().get(1), ChMap.tickets().get(2), ChMap.tickets().get(3)
        ));

        GameState gameState = GameState.initial(tickets, TestRandomizer.newRandom());

        assertEquals(Card.GREEN, gameState.topCard());
        assertEquals(SortedBag.of(List.of(Card.BLACK, Card.BLACK, Card.VIOLET, Card.LOCOMOTIVE)),
                gameState.currentPlayerState().cards());
        assertEquals(97, gameState.cardState().deckSize());

        gameState = gameState.withBlindlyDrawnCard();

        assertEquals(Card.ORANGE, gameState.topCard());
        assertEquals(SortedBag.of(List.of(Card.BLACK, Card.BLACK, Card.VIOLET, Card.LOCOMOTIVE, Card.GREEN)),
                gameState.currentPlayerState().cards());
        assertEquals(96, gameState.cardState().deckSize());
    }

    //------------------------------------------withClaimedRoute()--------------------------------------------------
    @Test
    void withClaimedRouteCheck(){
        SortedBag<Ticket> tickets = SortedBag.of(List.of(
                ChMap.tickets().get(0), ChMap.tickets().get(1), ChMap.tickets().get(2), ChMap.tickets().get(3)
        ));

        GameState gameState = GameState.initial(tickets, TestRandomizer.newRandom());
        assertEquals(0, gameState.cardState().discardsSize());
        assertEquals(List.of(), gameState.currentPlayerState().routes());
        assertEquals(List.of(), gameState.playerState(PlayerId.PLAYER_2).routes());

        gameState = gameState.withClaimedRoute(ChMap.routes().get(1), SortedBag.of(Card.RED));
        assertEquals(1, gameState.cardState().discardsSize());
        assertEquals(List.of(ChMap.routes().get(1)), gameState.currentPlayerState().routes());
        assertEquals(List.of(), gameState.playerState(PlayerId.PLAYER_1).routes());
    }

    //-------------------------------------------lastTurnBegins()------------------------------------------------
    @Test
    void lastTurnBeginsCheckFalse(){
        SortedBag<Ticket> tickets = SortedBag.of(List.of(
                ChMap.tickets().get(0), ChMap.tickets().get(1), ChMap.tickets().get(2), ChMap.tickets().get(3)
        ));

        GameState gameState = GameState.initial(tickets, TestRandomizer.newRandom());
        assertFalse(gameState.lastTurnBegins());
    }

    @Test
    void lastTurnBeginsCheckTrue(){
        SortedBag<Ticket> tickets = SortedBag.of(List.of(
                ChMap.tickets().get(0), ChMap.tickets().get(1), ChMap.tickets().get(2), ChMap.tickets().get(3)
        ));

        GameState gameState = GameState.initial(tickets, TestRandomizer.newRandom());
        int length=0;
        int i=0;
        do{
            gameState = gameState
                    .withClaimedRoute(ChMap.routes().get(i), SortedBag.of(ChMap.routes().get(i).possibleClaimCards().get(0)));
            length += ChMap.routes().get(i).length();
            i++;
        }while (length <= 35);

        assertEquals(4, gameState.currentPlayerState().carCount());

        gameState = gameState
                .withClaimedRoute(ChMap.routes().get(3), SortedBag.of(ChMap.routes().get(3).possibleClaimCards().get(0)));

        assertEquals(2, gameState.currentPlayerState().carCount());
        assertTrue(gameState.lastTurnBegins());
    }

    //------------------------------------------------forNextTurn()--------------------------------------------
    @Test
    void forNextTurnCheck1(){
        SortedBag<Ticket> tickets = SortedBag.of(List.of(
                ChMap.tickets().get(0), ChMap.tickets().get(1), ChMap.tickets().get(2), ChMap.tickets().get(3)
        ));

        GameState gameState = GameState.initial(tickets, TestRandomizer.newRandom());

        assertEquals(PlayerId.PLAYER_2, gameState.currentPlayerId());
        assertEquals(null, gameState.lastPlayer());

        gameState = gameState.forNextTurn();

        assertEquals(PlayerId.PLAYER_1, gameState.currentPlayerId());
        assertEquals(null, gameState.lastPlayer());
    }

    @Test
    void forNextTurnCheck2(){
        SortedBag<Ticket> tickets = SortedBag.of(List.of(
                ChMap.tickets().get(0), ChMap.tickets().get(1), ChMap.tickets().get(2), ChMap.tickets().get(3)
        ));

        GameState gameState = GameState.initial(tickets, TestRandomizer.newRandom());
        int length=0;
        int i=0;
        do{
            gameState = gameState
                    .withClaimedRoute(ChMap.routes().get(i), SortedBag.of(ChMap.routes().get(i).possibleClaimCards().get(0)));
            length += ChMap.routes().get(i).length();
            i++;
        }while (length <= 35);

        assertEquals(4, gameState.currentPlayerState().carCount());

        gameState = gameState
                .withClaimedRoute(ChMap.routes().get(3), SortedBag.of(ChMap.routes().get(3).possibleClaimCards().get(0)));

        assertEquals(2, gameState.currentPlayerState().carCount());
        assertTrue(gameState.lastTurnBegins());


        assertEquals(PlayerId.PLAYER_2, gameState.currentPlayerId());
        assertEquals(null, gameState.lastPlayer());

        gameState = gameState.forNextTurn();

        assertEquals(PlayerId.PLAYER_1, gameState.currentPlayerId());
        assertEquals(PlayerId.PLAYER_2, gameState.lastPlayer());
    }
}