package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.*;

import static ch.epfl.tchu.game.Route.Level.OVERGROUND;
import static ch.epfl.tchu.game.Route.Level.UNDERGROUND;

public final class GameChonb {

    public static void play(Map<PlayerId, Player> players,
                            Map<PlayerId, String> playerNames,
                            SortedBag<Ticket> tickets, Random rng) {

        Preconditions.checkArgument(playerNames.size() == 2 && players.size() == 2);

        GameState gameState = GameState.initial(tickets, rng);
        players.forEach((playerId, player) -> player.initPlayers(playerId, playerNames));

        Map<PlayerId, Info> playersInfo = new EnumMap<PlayerId, Info>(PlayerId.class);
        Map<PlayerId, SortedBag<Ticket>> playerTickets = new EnumMap<PlayerId, SortedBag<Ticket>>(PlayerId.class);

        playerNames.forEach(((playerId, player) -> playersInfo.put(playerId, new Info(player))));
        //Player currentPlayer = players.get(gameState.currentPlayerId);
        //Info information = playersInfo.get(gameState.currentPlayerId);

        getInfo(players, playersInfo.get(gameState.currentPlayerId()).willPlayFirst());

        for (Map.Entry<PlayerId, Player> actualPlayer : players.entrySet()) {
            actualPlayer.getValue().setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
            gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);

        }

        for (Map.Entry<PlayerId, Player> actualPlayer : players.entrySet()) {
            updateState(players, gameState);
            playerTickets.put(actualPlayer.getKey(), actualPlayer.getValue().chooseInitialTickets());
            gameState = gameState.withInitiallyChosenTickets(actualPlayer.getKey(), playerTickets.get(actualPlayer.getKey()));
        }

        //playerTickets.forEach((PlayerId, Tickets) -> Game.getInfo(players, playersInfo.get(gameState.currentPlayerId).keptTickets(Tickets.size())));


        int j=0;
        do {
            Player currentPlayer = players.get(gameState.currentPlayerId());
            Info information = playersInfo.get(gameState.currentPlayerId());
            updateState(players, gameState);
            getInfo(players, playersInfo.get(gameState.currentPlayerId()).canPlay());
            switch (currentPlayer.nextTurn()) {

                case DRAW_TICKETS:

                    getInfo(players, information.drewTickets(Constants.IN_GAME_TICKETS_COUNT));

                    SortedBag<Ticket> chosenTickets = currentPlayer.chooseTickets(gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT));

                    gameState = gameState.withChosenAdditionalTickets(gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT), chosenTickets);
                    getInfo(players, information.keptTickets(chosenTickets.size()));
                    break;
                case CLAIM_ROUTE:

                    Route route = currentPlayer.claimedRoute();
                    SortedBag<Card> claimCards = players.get(gameState.currentPlayerId()).initialClaimCards();

                    if (route.level().equals(UNDERGROUND)){

                        getInfo(players, information.attemptsTunnelClaim(route, claimCards));
                        SortedBag.Builder<Card> drawnCardsB = new SortedBag.Builder<>();

                        for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; i++) {
                            drawnCardsB.add(gameState.withCardsDeckRecreatedIfNeeded(rng).topCard());
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng).withoutTopCard();
                        }
                        SortedBag<Card> drawnCards = drawnCardsB.build();
                        int add = route.additionalClaimCardsCount(claimCards, drawnCards);
                        if (add == 0) {
                            getInfo(players, information.claimedRoute(route, claimCards));
                        } else {

                            SortedBag<Card> cards = gameState.currentPlayerState().cards();
                            List<SortedBag<Card>> possibleAddCards = gameState.currentPlayerState()
                                    .possibleAdditionalCards(add, claimCards, drawnCards);
                            SortedBag<Card> chosenCards = possibleAddCards.isEmpty() ? SortedBag.of() :
                                    currentPlayer.chooseAdditionalCards(possibleAddCards);

                            if (chosenCards.isEmpty()) {
                                getInfo(players, information.didNotClaimRoute(route));

                            } else {

                                SortedBag<Card> totalCardsToPlay = chosenCards.union(claimCards);

                                if (possibleAddCards.contains(totalCardsToPlay)) {

                                    gameState = gameState.withClaimedRoute(route, totalCardsToPlay);
                                    getInfo(players, information.claimedRoute(route, totalCardsToPlay));

                                } else {
                                    getInfo(players, information.didNotClaimRoute(route));
                                }
                            }
                        }
                        gameState = gameState.withMoreDiscardedCards(drawnCards);
                    } else if (route.level().equals(OVERGROUND)){
                        gameState=gameState.withClaimedRoute(route,claimCards);

                        getInfo(players, information.claimedRoute(route, claimCards));
                    }

                    break;
                case DRAW_CARDS:

                    for (int i = 0; i < 2; i++) {
                        if (i == 1) updateState(players, gameState);
                        int actualCard = currentPlayer.drawSlot();


                        if (Constants.FACE_UP_CARD_SLOTS.contains(actualCard)) {

                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng).withDrawnFaceUpCard(actualCard);

                            getInfo(players, information.drewVisibleCard(gameState.cardState().faceUpCard(actualCard)));

                        } else if (Constants.DECK_SLOT == actualCard) {

                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng).withBlindlyDrawnCard();

                            getInfo(players, information.drewBlindCard());
                        }
                    }break;
            }
            if (gameState.currentPlayerId().equals(gameState.lastPlayer())) {
                break;
            }
            if (gameState.lastTurnBegins()) {
                getInfo(players, playersInfo.get(gameState.currentPlayerId()).lastTurnBegins(gameState.currentPlayerState().carCount()));
            }
            gameState = gameState.forNextTurn();
        } while (true);

        Map<PlayerId, Trail> longestTrailMap = new EnumMap<PlayerId, Trail>(PlayerId.class);
        Map<PlayerId, Integer> pointsMap = new EnumMap<PlayerId, Integer>(PlayerId.class);

        for (Map.Entry<PlayerId, Player> actualPlayer : players.entrySet()) {
            longestTrailMap.put(actualPlayer.getKey(), Trail.longest(gameState.playerState(actualPlayer.getKey()).routes()));
            pointsMap.put(actualPlayer.getKey(), gameState.playerState(actualPlayer.getKey()).finalPoints());
        }

        int comparator = Integer.compare(longestTrailMap.get(PlayerId.PLAYER_1).length(), longestTrailMap.get(PlayerId.PLAYER_2).length());

        switch (comparator) {

            case 0:
                getInfo(players, playersInfo.get(PlayerId.PLAYER_1).getsLongestTrailBonus(longestTrailMap.get(PlayerId.PLAYER_1)));
                getInfo(players, playersInfo.get(PlayerId.PLAYER_2).getsLongestTrailBonus(longestTrailMap.get(PlayerId.PLAYER_2)));
                pointsMap.replace(PlayerId.PLAYER_1, pointsMap.get(PlayerId.PLAYER_1) + Constants.LONGEST_TRAIL_BONUS_POINTS);
                pointsMap.replace(PlayerId.PLAYER_2, pointsMap.get(PlayerId.PLAYER_2) + Constants.LONGEST_TRAIL_BONUS_POINTS);

            case 1:
                getInfo(players, playersInfo.get(PlayerId.PLAYER_1).getsLongestTrailBonus(longestTrailMap.get(PlayerId.PLAYER_1)));
                pointsMap.replace(PlayerId.PLAYER_1, pointsMap.get(PlayerId.PLAYER_1) + Constants.LONGEST_TRAIL_BONUS_POINTS);

            case -1:
                getInfo(players, playersInfo.get(PlayerId.PLAYER_2).getsLongestTrailBonus(longestTrailMap.get(PlayerId.PLAYER_2)));
                pointsMap.replace(PlayerId.PLAYER_2, pointsMap.get(PlayerId.PLAYER_2) + Constants.LONGEST_TRAIL_BONUS_POINTS);
        }

        //------Victoire-----//

        int pointsComparator = Integer.compare(pointsMap.get(PlayerId.PLAYER_1), pointsMap.get(PlayerId.PLAYER_2));
        List<String> namesList = new ArrayList<>();
        for (Map.Entry<PlayerId, String> actualPlayer : playerNames.entrySet()) {
            namesList.add(actualPlayer.getValue());
        }

        updateState(players, gameState);

        switch (pointsComparator) {

            case 0:
                getInfo(players, Info.draw(namesList, pointsMap.get(PlayerId.PLAYER_1)));
            case 1:
                getInfo(players, playersInfo.get(gameState.currentPlayerId()).won(pointsMap.get(PlayerId.PLAYER_1), pointsMap.get(PlayerId.PLAYER_2)));
            case -1:
                getInfo(players, playersInfo.get(gameState.currentPlayerId()).won(pointsMap.get(PlayerId.PLAYER_2), pointsMap.get(PlayerId.PLAYER_1)));

        }
    }

    private static void getInfo(Map<PlayerId, Player> players, String string) {
        players.get(PlayerId.PLAYER_1).receiveInfo(string);
        players.get(PlayerId.PLAYER_2).receiveInfo(string);
    }

    private static void updateState(Map<PlayerId, Player> players, GameState alo) {
        players.forEach((playerId, player) -> player.updateState(alo, alo.playerState(playerId)));
    }

}