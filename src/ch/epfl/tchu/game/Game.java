package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.*;
import java.util.List;

import static ch.epfl.tchu.game.Constants.*;


public final class Game{

    public static final int PLAYERS_COUNT = 2;

    public static void play(Map<PlayerId, Player> players, Map<PlayerId,
                            String> playerNames, SortedBag<Ticket> tickets, Random rng){

        Preconditions.checkArgument(players.size()==PLAYERS_COUNT && playerNames.size()==PLAYERS_COUNT);
        // Initialisation
        Map<PlayerId,Info> playerInformation = new EnumMap<>(PlayerId.class);
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

        //----------------------------- Commencement de la partie ------------------------------------------------------

        receiveInfoForBothPlayers(players,playerInformation.get(gameState.currentPlayerId()).canPlay());

        //TODO condition de fin de partie a verifier

        while (!gameState.currentPlayerId().equals(gameState.lastPlayer())){

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
                    receiveInfoForBothPlayers(players, information.keptTickets(
                            currentPlayer.chooseTickets((gameState.topTickets(IN_GAME_TICKETS_COUNT))).size()));
                    gameState = gameState.withChosenAdditionalTickets(
                                gameState.topTickets(IN_GAME_TICKETS_COUNT), playerTickets);
                    //TODO update?
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
                    switch (chosenRoute.level()){
                        case OVERGROUND:
                            // Ajout de la route et retrait des cartes
                            gameState = gameState.withClaimedRoute(chosenRoute, playerClaimCards);
                            // Affichage et mise à jour
                            updateStateForBothPlayers(players, gameState);
                            receiveInfoForBothPlayers(players, information
                                    .claimedRoute(chosenRoute, playerClaimCards));
                            break;
                        case UNDERGROUND:
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
                            break;
                    }
                    break;
            }
          gameState = gameState.forNextTurn();
        }

        //-------------------------------------- Fin de la partie ------------------------------------------------------

        // Informe les joueurs du résultat final de la partie
        updateStateForBothPlayers(players,gameState);
        GameState finalGameState = gameState ;

        // Map avec les points du joueur
        Map<PlayerId, Integer> playerPoints = new EnumMap<>(PlayerId.class);
        playerPoints.forEach( (k,v) -> playerPoints.put(k,finalGameState.playerState(k).finalPoints()));

        // Determine le chemin le plus long et attribue le bonus
        longestDeclaration(players, playerPoints, playerInformation, gameState);

        // Determine la victoire d'un joueur ou une égalité
        winnerDeclaration(players,playerNames,playerPoints,playerInformation);
    }

    /**
     * Transmet les informations de la partie aux deux joueurs
     *
     * @param players Map des joueurs
     * @param info Type d'information transmis
     */
    private static void receiveInfoForBothPlayers(Map<PlayerId, Player> players, String info){
        players.get(PlayerId.PLAYER_1).receiveInfo(info);
        players.get(PlayerId.PLAYER_2).receiveInfo(info);
    }

    /**
     * Met à jour l'état de jeu pour les joueurs, en appelant la méthode updateState de chacun d'eux.
     *
     * @param players Map des joueurs
     * @param newState Nouvel état de jeu
     */
    private static void updateStateForBothPlayers(Map<PlayerId, Player> players,GameState newState){
        for (Map.Entry<PlayerId, Player> entry : players.entrySet()){
            entry.getValue().updateState(newState,newState.playerState(entry.getKey()));
        }
        //players.forEach((k,v) -> v.updateState(newState,newState.playerState(k)));
    }

    /**
     * Methode permettant de calculer le plus long chemin et d'attribuer les points bonus aux joueurs
     *
     * @param players Map des joueurs
     * @param playerPoints Map des points des joueurs
     * @param playerInformation Map des informations des joueurs
     * @param gameState Dernier etat de jeu
     */
    private static void longestDeclaration(Map<PlayerId, Player> players,
                                           Map<PlayerId, Integer> playerPoints,
                                           Map<PlayerId, Info> playerInformation,
                                           GameState gameState){

        Map<PlayerId,Trail> playerLongestTrails = new EnumMap<>(PlayerId.class);
        playerLongestTrails.forEach( (k,v) -> playerLongestTrails.put(k,Trail.longest(gameState.playerState(k).routes()))); // en fonction du joueur

        int conditionTrail = Integer.compare(playerLongestTrails.get(PlayerId.PLAYER_1).length(),playerLongestTrails.get(PlayerId.PLAYER_2).length());

        switch (conditionTrail) {
            case (0):        // Meme longueur
                receiveInfoForBothPlayers(players,playerInformation.get(PlayerId.PLAYER_1).getsLongestTrailBonus(playerLongestTrails.get(PlayerId.PLAYER_1)));
                receiveInfoForBothPlayers(players,playerInformation.get(PlayerId.PLAYER_2).getsLongestTrailBonus(playerLongestTrails.get(PlayerId.PLAYER_2)));
                playerPoints.replace(PlayerId.PLAYER_1 , playerPoints.get(PlayerId.PLAYER_1)+ LONGEST_TRAIL_BONUS_POINTS);
                playerPoints.replace(PlayerId.PLAYER_2 , playerPoints.get(PlayerId.PLAYER_2)+ LONGEST_TRAIL_BONUS_POINTS);
                break;
            case (1):        // Joueur 1 a le bonus
                receiveInfoForBothPlayers(players,playerInformation.get(PlayerId.PLAYER_1).getsLongestTrailBonus(playerLongestTrails.get(PlayerId.PLAYER_1)));
                playerPoints.replace(PlayerId.PLAYER_1 , playerPoints.get(PlayerId.PLAYER_1)+ LONGEST_TRAIL_BONUS_POINTS);
                break;
            case (-1):        // Joueur 2 a le bonus
                receiveInfoForBothPlayers(players,playerInformation.get(PlayerId.PLAYER_2).getsLongestTrailBonus(playerLongestTrails.get(PlayerId.PLAYER_2)));
                playerPoints.replace(PlayerId.PLAYER_2 , playerPoints.get(PlayerId.PLAYER_2)+ LONGEST_TRAIL_BONUS_POINTS);
                break;
        }
    }

    /**
     * Methode permettant d'afficher le joueur gagnant ou, s'il y a égalité
     *
     * @param players Map des joueurs
     * @param playerNames Map des noms des joueurs
     * @param playerPoints Map des points des joueurs
     * @param playerInformation Map des informations des joueurs
     */
    private static void winnerDeclaration(Map<PlayerId, Player> players,
                                          Map<PlayerId, String> playerNames,
                                          Map<PlayerId, Integer> playerPoints,
                                          Map<PlayerId, Info> playerInformation){

        ArrayList<String> playerName = new ArrayList<>(playerNames.values());
        int conditionPoint = Integer.compare(playerPoints.get(PlayerId.PLAYER_1),playerPoints.get(PlayerId.PLAYER_2));

        switch (conditionPoint){
            case(0):        // Egalité
                receiveInfoForBothPlayers(players,Info.draw(playerName,playerPoints.get(PlayerId.PLAYER_1)));
                break;
            case(1):        // Joueur 1 gagne
                receiveInfoForBothPlayers(players,playerInformation.get(PlayerId.PLAYER_1).won(
                        playerPoints.get(PlayerId.PLAYER_1),
                        playerPoints.get(PlayerId.PLAYER_2)));
                break;
            case(-1):        // Joueur 2 gagne
                receiveInfoForBothPlayers(players,playerInformation.get(PlayerId.PLAYER_2).won(
                        playerPoints.get(PlayerId.PLAYER_2),
                        playerPoints.get(PlayerId.PLAYER_1)));
                break;
        }
    }
}
