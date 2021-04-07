package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.awt.*;
import java.util.*;
import java.util.List;

import static ch.epfl.tchu.game.Constants.*;
import static ch.epfl.tchu.game.Constants.ADDITIONAL_TUNNEL_CARDS;
import static ch.epfl.tchu.game.Player.TurnKind.*;


public final class Game{

    public static final int PLAYERS_COUNT = 2;



    public static void play(Map<PlayerId, Player> players, Map<PlayerId,
            String> playerNames, SortedBag<Ticket> tickets, Random rng){

        Preconditions.checkArgument(players.size()==PLAYERS_COUNT && playerNames.size()==PLAYERS_COUNT);

        // Initialisation
        Map<PlayerId,Info> playerInformation = new EnumMap<>(PlayerId.class);                   //
        playerNames.forEach((player,info) -> playerInformation.put(player, new Info(info)));    // Informations des joueurs
        Map<PlayerId,SortedBag<Ticket>> playersTickets = new EnumMap<>(PlayerId.class);         // Tickets initiaux
        players.forEach((key,value) -> value.initPlayers(key,playerNames));                     // Joueurs
        GameState gameState = GameState.initial(tickets,rng);                                   // Etat de jeu

        receiveInfoForBothPlayers(players,playerInformation.get(gameState.currentPlayerId()).willPlayFirst());

        // Distribution des premiers tickets aux joueurs
        for (Map.Entry<PlayerId, Player> entry : players.entrySet()){
            entry.getValue().setInitialTicketChoice(gameState.topTickets(INITIAL_TICKETS_COUNT));
            gameState = gameState.withoutTopTickets(INITIAL_TICKETS_COUNT);
        }

        updateStateForBothPlayers(players, gameState);

        // Les joueurs gardent les tickets qu'ils choisissent
        for (Map.Entry<PlayerId, Player> entry : players.entrySet()){
            playersTickets.put(entry.getKey(),entry.getValue().chooseInitialTickets());
            gameState = gameState.withInitiallyChosenTickets(entry.getKey(),playersTickets.get(entry.getKey()));
        }
        players.forEach( (key,value)-> receiveInfoForBothPlayers(players,
                playerInformation.get(key).keptTickets(playersTickets.get(key).size())));



        //-------------------------- commencement de la partie ----------------------------



        receiveInfoForBothPlayers(players,playerInformation.get(gameState.currentPlayerId()).canPlay());

        //TODO condition de fin de partie a verifier

        while (!gameState.lastTurnBegins() && !gameState.currentPlayerId().equals(gameState.lastPlayer())){

            Player currentPlayer = players.get(gameState.currentPlayerId());   // initialisation du joueur actuel
            Info information = playerInformation.get(gameState.currentPlayerId()); // initialisation information du joueur

            updateStateForBothPlayers(players, gameState);

            if(gameState.lastTurnBegins()){
                int carCount = gameState.playerState(gameState.lastPlayer()).carCount();
                receiveInfoForBothPlayers(players,playerInformation.get(gameState.lastPlayer()).lastTurnBegins(carCount));
            }

            switch (players.get(gameState.currentPlayerId()).nextTurn()) {

                case DRAW_TICKETS:

                    receiveInfoForBothPlayers(players, information.drewTickets(IN_GAME_TICKETS_COUNT));

                    SortedBag<Ticket> playerTickets = currentPlayer.chooseTickets(gameState.topTickets(IN_GAME_TICKETS_COUNT));

                    receiveInfoForBothPlayers(players,
                            information.keptTickets(currentPlayer.chooseTickets((gameState.topTickets(IN_GAME_TICKETS_COUNT))).size()));

                    gameState = gameState.withChosenAdditionalTickets(gameState.topTickets(IN_GAME_TICKETS_COUNT),
                            playerTickets);


                    break;
                case DRAW_CARDS:
                    for (int i = 0; i < 2; i++) {
                        int actualDrawSlot = currentPlayer.drawSlot();          // Slot que le joueur va tirer 2 fois
                        updateStateForBothPlayers(players, gameState);
                        if (FACE_UP_CARD_SLOTS.contains(actualDrawSlot)) {          // Tire des face up cards
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng)
                                    .withDrawnFaceUpCard(currentPlayer.drawSlot());

                            Card pickedCard = gameState.cardState().faceUpCard(actualDrawSlot);
                            updateStateForBothPlayers(players, gameState);
                            receiveInfoForBothPlayers(players, information.drewVisibleCard(pickedCard));
                        } else if (actualDrawSlot == Constants.DECK_SLOT) {          // Tire du Deck
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng)
                                    .withBlindlyDrawnCard();

                            updateStateForBothPlayers(players, gameState);
                            receiveInfoForBothPlayers(players, information.drewBlindCard());
                        }
                    }
                    break;
                case CLAIM_ROUTE:
                    // Initialisation
                    Route chosenRoute = currentPlayer.claimedRoute();
                    SortedBag<Card> playerClaimCards = currentPlayer.initialClaimCards();

                    if (chosenRoute.level().equals(Route.Level.OVERGROUND)) {    // Route en surface

                        // Ajout de la route et retrait des cartes
                        gameState = gameState.withClaimedRoute(chosenRoute, playerClaimCards);
                        // Affichage et mise à jour
                        updateStateForBothPlayers(players, gameState);
                        receiveInfoForBothPlayers(players, information
                                .claimedRoute(chosenRoute, playerClaimCards));
                    } else if (chosenRoute.level().equals(Route.Level.UNDERGROUND)) {    // Route en tunnel

                        // Affichage pour le tunnel
                        receiveInfoForBothPlayers(players, information
                                .attemptsTunnelClaim(chosenRoute, playerClaimCards));
                        // Création des DrawnCards
                        SortedBag.Builder<Card> drawnCardsBuilder = new SortedBag.Builder<>();
                        for (int i = 0; i < ADDITIONAL_TUNNEL_CARDS; i++) {
                            drawnCardsBuilder.add(gameState.withCardsDeckRecreatedIfNeeded(rng).topCard());
                        }
                        SortedBag<Card> drawnCards = drawnCardsBuilder.build();
                        int addClaimCardsCount = chosenRoute.additionalClaimCardsCount(playerClaimCards, drawnCards);

                        gameState = gameState.withoutTopCard().withoutTopCard().withoutTopCard().withMoreDiscardedCards(drawnCards);
                        updateStateForBothPlayers(players, gameState);
                        // Affichage des cartes additionnelles tirées
                        receiveInfoForBothPlayers(players, information
                                .drewAdditionalCards(drawnCards, addClaimCardsCount));
                        // Cartes que le joueur peut jouer
                        List<SortedBag<Card>> playableCards = gameState.currentPlayerState()
                                .possibleAdditionalCards(addClaimCardsCount, playerClaimCards, drawnCards);
                        // Cartes que le joueur va jouer
                        SortedBag<Card> playedAddCards = currentPlayer.chooseAdditionalCards(playableCards);
                        if (playedAddCards.isEmpty()) {    // Tentative échouée
                            updateStateForBothPlayers(players, gameState);
                            receiveInfoForBothPlayers(players, information.didNotClaimRoute(chosenRoute));
                        } else {                            // Tentative réussie
                            // Ajout de la route et retrait des cartes
                            gameState = gameState.withClaimedRoute(chosenRoute, playedAddCards.union(playerClaimCards));
                            updateStateForBothPlayers(players, gameState);
                            receiveInfoForBothPlayers(players, information.claimedRoute(chosenRoute, playedAddCards.union(playerClaimCards)));
                        }
                    }
                    break;
            }

            gameState.forNextTurn();

        }
      //TODO condition de fin de partie
      //
      //
      //
                updateStateForBothPlayers(players,gameState);// informe les joueurs du résultat final de la partie

        Map<PlayerId,Trail> playerLongestTrails = new EnumMap<>(PlayerId.class);     //map avec le trail le plus long
                                                                                     // en fonction du joueur
        for ( Map.Entry<PlayerId, Player> entry : players.entrySet() ) {
            playerLongestTrails.put(entry.getKey(),Trail.longest(gameState.playerState(entry.getKey()).routes()));
        }

        Map<PlayerId, Integer> playerPoints = new EnumMap<>(PlayerId.class);   //map avec les points du joueur
                                                                               // en fonction des routes possédées
        for ( Map.Entry<PlayerId, Player> entry : players.entrySet() ) {
            playerPoints.put(entry.getKey(),gameState.playerState(entry.getKey()).finalPoints());
        }

        // comparons les chemins les plus long
        // et attribuons les points bonus au bon joueur
        //ici le plus long chemin des deux joueurs ont la même taille .
            if(playerLongestTrails.get(PlayerId.PLAYER_1).length() == playerLongestTrails.get(PlayerId.PLAYER_2).length()){

          receiveInfoForBothPlayers(players,playerInformation.get(PlayerId.PLAYER_1).getsLongestTrailBonus(playerLongestTrails.get(PlayerId.PLAYER_1)));
          receiveInfoForBothPlayers(players,playerInformation.get(PlayerId.PLAYER_2).getsLongestTrailBonus(playerLongestTrails.get(PlayerId.PLAYER_2)));

          playerPoints.replace(PlayerId.PLAYER_1 , playerPoints.get(PlayerId.PLAYER_1)+ LONGEST_TRAIL_BONUS_POINTS);
          playerPoints.replace(PlayerId.PLAYER_2 , playerPoints.get(PlayerId.PLAYER_2)+ LONGEST_TRAIL_BONUS_POINTS);
            }else if(playerLongestTrails.get(PlayerId.PLAYER_1).length() < playerLongestTrails.get(PlayerId.PLAYER_2).length()){
                receiveInfoForBothPlayers(players,playerInformation.get(PlayerId.PLAYER_2).getsLongestTrailBonus(playerLongestTrails.get(PlayerId.PLAYER_2)));

                playerPoints.replace(PlayerId.PLAYER_2 , playerPoints.get(PlayerId.PLAYER_2)+ LONGEST_TRAIL_BONUS_POINTS);

            }else{
                receiveInfoForBothPlayers(players,playerInformation.get(PlayerId.PLAYER_1).getsLongestTrailBonus(playerLongestTrails.get(PlayerId.PLAYER_1)));

                playerPoints.replace(PlayerId.PLAYER_1 , playerPoints.get(PlayerId.PLAYER_1)+ LONGEST_TRAIL_BONUS_POINTS);
            }


            // victoire, défaite ,æquo

        ArrayList<String> playerName =new ArrayList<>(playerNames.values());

            if(playerPoints.get(PlayerId.PLAYER_1).equals(playerPoints.get(PlayerId.PLAYER_2))){

                receiveInfoForBothPlayers(players,Info.draw(playerName,playerPoints.get(PlayerId.PLAYER_1)));

            }else if( playerPoints.get(PlayerId.PLAYER_1) > (playerPoints.get(PlayerId.PLAYER_2))){
                receiveInfoForBothPlayers(players,playerInformation.get(PlayerId.PLAYER_1).won(playerPoints.get(PlayerId.PLAYER_1),
                        playerPoints.get(PlayerId.PLAYER_2)));

            }else{

                receiveInfoForBothPlayers(players,playerInformation.get(PlayerId.PLAYER_2).won(playerPoints.get(PlayerId.PLAYER_2),
                        playerPoints.get(PlayerId.PLAYER_1)));

            }

    }


    /**
     *
     * @param players
     * @param info
     */
    private static void receiveInfoForBothPlayers(Map<PlayerId, Player> players, String info){
        players.get(PlayerId.PLAYER_1).receiveInfo(info);
        players.get(PlayerId.PLAYER_2).receiveInfo(info);
    }

    /**
     * méthode permettant d'informer tous les joueurs d'un changement d'état,
     * en appelant la méthode updateState de chacun d'eux.
     * @param players
     * @param newState
     */
    private static void updateStateForBothPlayers(Map<PlayerId, Player> players,GameState newState){
        for (Map.Entry<PlayerId, Player> entry : players.entrySet()){
            entry.getValue().updateState(newState,newState.playerState(entry.getKey()));
        }
        //TODO A voir
        //players.forEach((k,v) -> v.updateState(newState,newState.playerState(k)));
    }

}
