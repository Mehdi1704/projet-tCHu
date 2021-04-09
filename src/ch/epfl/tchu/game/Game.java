package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.*;
import java.util.List;

import static ch.epfl.tchu.game.Constants.*;


public final class Game {

    public static final int PLAYERS_COUNT = 2;

    /**
     * methode permettant le déroulement de la partie :
     *
     *  1 phase : l'initialisation de l'etat du jeu,distribution des cartes, détermination du premier joueur
     *  et distribution des tickets ainsi que du choix de ces derniers .
     *
     * 2 phase : début de partie ou les joueurs vont à tour de rôle effectuer diverses actions,
     *  soit piocher,et sélectionner des tickets , soit piocher des cartes , soit tenter de s'emparer d'une route.
     *
     *  3 phase : détermination du nombre de points de chaque joueur , et donc détermination du vainqueur .
     *
     * @param players map des joueurs
     * @param playerNames map des noms de nos joueurs
     * @param tickets les tickets que l'on va utiliser durant la partie
     * @param rng variable permettant, de mélanger les cartes,et de choisir le joueur qui joue en premier,
     *           tout cela de manière aléatoire
     */
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

        // Les joueurs gardent les tickets qu'ils choisissent
        for (Map.Entry<PlayerId, Player> entry : players.entrySet()){
            updateStateForBothPlayers(players, gameState);
            playersTickets.put(entry.getKey(),entry.getValue().chooseInitialTickets());
            gameState = gameState.withInitiallyChosenTickets(entry.getKey(),playersTickets.get(entry.getKey()));
        }
        players.forEach( (key,value)-> receiveInfoForBothPlayers(players,
                playerInformation.get(key).keptTickets(playersTickets.get(key).size())));

        //----------------------------- Commencement de la partie ------------------------------------------------------

        while (true){

            Player currentPlayer = players.get(gameState.currentPlayerId());        // initialisation du joueur actuel
            Info information = playerInformation.get(gameState.currentPlayerId());  // initialisation information du joueur
            receiveInfoForBothPlayers(players,playerInformation.get(gameState.currentPlayerId()).canPlay());
            updateStateForBothPlayers(players, gameState);

            switch (players.get(gameState.currentPlayerId()).nextTurn()) {

                case DRAW_TICKETS:
                    receiveInfoForBothPlayers(players, information.drewTickets(IN_GAME_TICKETS_COUNT));
                    SortedBag<Ticket> playerTickets = currentPlayer.chooseTickets(gameState.topTickets(IN_GAME_TICKETS_COUNT));
                    receiveInfoForBothPlayers(players, information.keptTickets(playerTickets.size()));
                    gameState = gameState.withChosenAdditionalTickets(
                                gameState.topTickets(IN_GAME_TICKETS_COUNT), playerTickets);
                    break;

                case DRAW_CARDS:
                    for (int i = 0; i < 2; i++) {
                        int actualDrawSlot = currentPlayer.drawSlot();          // Slot que le joueur va tirer 2 fois
                        updateStateForBothPlayers(players, gameState);
                        if (FACE_UP_CARD_SLOTS.contains(actualDrawSlot)) {          // Tire des face up cards
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng)
                                    .withDrawnFaceUpCard(actualDrawSlot);

                            Card pickedCard = gameState.cardState().faceUpCard(actualDrawSlot);
                            receiveInfoForBothPlayers(players, information.drewVisibleCard(pickedCard));
                        } else if (actualDrawSlot == Constants.DECK_SLOT) {          // Tire du Deck
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng)
                                    .withBlindlyDrawnCard();
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
                                gameState = gameState.withCardsDeckRecreatedIfNeeded(rng).withoutTopCard();
                            }
                            SortedBag<Card> drawnCards = drawnCardsBuilder.build();
                            gameState = gameState.withMoreDiscardedCards(drawnCards);
                            int addClaimCardsCount = chosenRoute.additionalClaimCardsCount(playerClaimCards, drawnCards);
                            // Affichage des cartes additionnelles tirées
                            receiveInfoForBothPlayers(players, information
                                    .drewAdditionalCards(drawnCards, addClaimCardsCount));
                            if (addClaimCardsCount == 0){
                                // Ajout de la route et retrait des cartes initiales
                                gameState = gameState.withClaimedRoute(chosenRoute, playerClaimCards);
                                receiveInfoForBothPlayers(players, information.claimedRoute(chosenRoute, playerClaimCards));
                            }else {
                                // Cartes que le joueur peut jouer
                                List<SortedBag<Card>> playableCards = gameState.currentPlayerState()
                                        .possibleAdditionalCards(addClaimCardsCount, playerClaimCards, drawnCards);
                                // Cartes que le joueur va jouer
                                SortedBag<Card> playedAddCards = currentPlayer.chooseAdditionalCards(playableCards);
                                if (playedAddCards==null) {    // Tentative échouée
                                    receiveInfoForBothPlayers(players, information.didNotClaimRoute(chosenRoute));
                                } else {                            // Tentative réussie
                                    // Ajout de la route et retrait des cartes
                                    gameState = gameState.withClaimedRoute(chosenRoute, playedAddCards.union(playerClaimCards));
                                    receiveInfoForBothPlayers(players, information.claimedRoute(chosenRoute, playedAddCards.union(playerClaimCards)));
                                }
                            }
                            break;
                    }
                    break;
            }

            if(gameState.currentPlayerId().equals(gameState.lastPlayer())){
                break;
            }
            if(gameState.lastTurnBegins()){
                int carCount = gameState.playerState(gameState.currentPlayerId()).carCount();
                receiveInfoForBothPlayers(players,playerInformation.get(gameState.currentPlayerId())
                        .lastTurnBegins(carCount));
            }
            gameState = gameState.forNextTurn();
        }

        //-------------------------------------- Fin de la partie ------------------------------------------------------

        // Informe les joueurs du résultat final de la partie
        updateStateForBothPlayers(players,gameState);
        // Initialisation:
        // Map des chemins les plus longs
        Map<PlayerId,Trail> playerLongestTrails = new EnumMap<>(PlayerId.class);
        // Map des points des joueurs
        Map<PlayerId, Integer> playerPoints = new EnumMap<>(PlayerId.class);
        for ( Map.Entry<PlayerId, Player> entry : players.entrySet() ) {
            playerLongestTrails.put(entry.getKey(),Trail.longest(gameState.playerState(entry.getKey()).routes()));
            playerPoints.put(entry.getKey(),gameState.playerState(entry.getKey()).finalPoints());
        }
        // Determine le chemin le plus long et attribue le bonus
        longestDeclaration(players, playerPoints, playerInformation, playerLongestTrails);
        // Determine la victoire d'un joueur ou une égalité
        winnerDeclaration(players, playerNames, playerPoints, playerInformation);
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
     * @param playerLongestTrails Map des chemins les plus longs
     */
    private static void longestDeclaration(Map<PlayerId, Player> players,
                                           Map<PlayerId, Integer> playerPoints,
                                           Map<PlayerId, Info> playerInformation,
                                           Map<PlayerId,Trail> playerLongestTrails){

        int conditionTrail = Integer.compare(playerLongestTrails.get(PlayerId.PLAYER_1).length(),playerLongestTrails.get(PlayerId.PLAYER_2).length());
        switch (conditionTrail) {
            case (0):        // Meme longueur
                annexeLongestDeclaration(players,playerPoints, playerInformation, playerLongestTrails, PlayerId.PLAYER_1);
                annexeLongestDeclaration(players,playerPoints, playerInformation, playerLongestTrails, PlayerId.PLAYER_2);
                break;
            case (1):        // Joueur 1 a le bonus
                annexeLongestDeclaration(players,playerPoints, playerInformation, playerLongestTrails, PlayerId.PLAYER_1);
                break;
            case (-1):        // Joueur 2 a le bonus
                annexeLongestDeclaration(players,playerPoints, playerInformation, playerLongestTrails, PlayerId.PLAYER_2);
                break;
        }
    }

    private static void annexeLongestDeclaration(Map<PlayerId, Player> players,
                                                 Map<PlayerId, Integer> playerPoints,
                                                 Map<PlayerId, Info> playerInformation,
                                                 Map<PlayerId,Trail> playerLongestTrails,
                                                 PlayerId playerID){
        receiveInfoForBothPlayers(players,playerInformation.get(playerID).getsLongestTrailBonus(playerLongestTrails.get(playerID)));
        playerPoints.replace(playerID , playerPoints.get(playerID)+ LONGEST_TRAIL_BONUS_POINTS);

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
