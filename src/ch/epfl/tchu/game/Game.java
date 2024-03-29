package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.AudioPlayer;
import ch.epfl.tchu.gui.Info;

import java.util.*;
import java.util.List;

import static ch.epfl.tchu.game.Constants.*;

/**
 * Classe qui fait tourner le jeu
 *
 * @author Mehdi Bouchoucha (314843)
 * @author Ali Ridha Mrad (314529)
 */
public final class Game {

    public static final int DRAW_CARDS_COUNT = 2;

    private Game() {
    }

    /**
     * Methode permettant le déroulement de la partie :
     * <p>
     * Phase 1: l'initialisation de l'etat du jeu, distribution des cartes, détermination du premier joueur
     * et distribution des tickets ainsi que du choix de ces derniers.
     * <p>
     * Phase 2: début de partie ou les joueurs vont à tour de rôle effectuer diverses actions,
     * soit piocher,et sélectionner des tickets , soit piocher des cartes , soit tenter de s'emparer d'une route.
     * <p>
     * Phase 3: détermination du nombre de points de chaque joueur , et donc détermination du vainqueur .
     *
     * @param players     Map des joueurs
     * @param playerNames Map des noms de nos joueurs
     * @param tickets     Tickets que l'on va utiliser durant la partie
     * @param rng         Variable permettant, de mélanger les cartes,et de choisir le joueur qui joue en premier,
     *                    tout cela de manière aléatoire
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames,
                            SortedBag<Ticket> tickets, Random rng) {

        Preconditions.checkArgument(players.size() == PlayerId.COUNT);
        Preconditions.checkArgument(playerNames.size() == PlayerId.COUNT);
        // Initialisation
        Map<PlayerId, Info> playerInformation = new EnumMap<>(PlayerId.class);
        // Informations des joueurs
        playerNames.forEach((player, info) -> playerInformation.put(player, new Info(info)));
        // Tickets initiaux
        Map<PlayerId, SortedBag<Ticket>> playersTickets = new EnumMap<>(PlayerId.class);
        //BONUS: couleurs et constanes
        Map<PlayerId, String> playersColors = new EnumMap<>(PlayerId.class);
        playersColors.put(PlayerId.PLAYER_1, colorPlayer1.toString());
        playersColors.put(PlayerId.PLAYER_2, colorPlayer2.toString());
        // Joueurs
        players.forEach((k, v) -> {
            v.initConstants(playersColors, constantsList);
            v.initPlayers(k, playerNames);
        });
        // Etat de jeu
        GameState gameState = GameState.initial(tickets, rng);
        updateStateForBothPlayers(players, gameState);
        receiveInfoForBothPlayers(players, playerInformation.get(gameState.currentPlayerId()).willPlayFirst());

        // Distribution des premiers tickets aux joueurs
        for (Map.Entry<PlayerId, Player> entry : players.entrySet()) {
            entry.getValue().setInitialTicketChoice(gameState.topTickets(INITIAL_TICKETS_COUNT));
            gameState = gameState.withoutTopTickets(INITIAL_TICKETS_COUNT);
        }

        updateStateForBothPlayers(players, gameState);
        // Les joueurs gardent les tickets qu'ils choisissent
        for (Map.Entry<PlayerId, Player> entry : players.entrySet()) {
            playersTickets.put(entry.getKey(), entry.getValue().chooseInitialTickets());
            gameState = gameState.withInitiallyChosenTickets(entry.getKey(), playersTickets.get(entry.getKey()));
        }
        players.forEach((key, value) -> receiveInfoForBothPlayers(players,
                playerInformation.get(key).keptTickets(playersTickets.get(key).size())));

        //----------------------------- Commencement de la partie ------------------------------------------------------

        while (true) {
            // Initialisation du joueur actuel
            Player currentPlayer = players.get(gameState.currentPlayerId());
            // Initialisation information du joueur
            Info information = playerInformation.get(gameState.currentPlayerId());
            receiveInfoForBothPlayers(players, information.canPlay());
            updateStateForBothPlayers(players, gameState);
            // Action que le joueur veut effectuer durant ce tour
            Player.TurnKind typeOfAction = players.get(gameState.currentPlayerId()).nextTurn();
            switch (typeOfAction) {

                case DRAW_TICKETS:
                    receiveInfoForBothPlayers(players, information.drewTickets(IN_GAME_TICKETS_COUNT));
                    SortedBag<Ticket> playerTickets = currentPlayer.chooseTickets(gameState.topTickets(IN_GAME_TICKETS_COUNT));
                    receiveInfoForBothPlayers(players, information.keptTickets(playerTickets.size()));
                    gameState = gameState.withChosenAdditionalTickets(
                            gameState.topTickets(IN_GAME_TICKETS_COUNT), playerTickets);
                    break;

                case DRAW_CARDS:
                    for (int i = 0; i < DRAW_CARDS_COUNT; i++) {
                        // Slot que le joueur va tirer 2 fois
                        int actualDrawSlot = currentPlayer.drawSlot();
                        updateStateForBothPlayers(players, gameState);
                        // Tire des face up cards
                        if (FACE_UP_CARD_SLOTS.contains(actualDrawSlot)) {
                            Card pickedCard = gameState.cardState().faceUpCard(actualDrawSlot);
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng)
                                    .withDrawnFaceUpCard(actualDrawSlot);
                            updateStateForBothPlayers(players, gameState);
                            receiveInfoForBothPlayers(players, information.drewVisibleCard(pickedCard));

                            // Tire du Deck
                        } else if (actualDrawSlot == Constants.DECK_SLOT) {
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
                    switch (chosenRoute.level()) {
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
                                gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                                drawnCardsBuilder.add(gameState.topCard());
                                gameState = gameState.withoutTopCard();
                            }
                            SortedBag<Card> drawnCards = drawnCardsBuilder.build();
                            gameState = gameState.withMoreDiscardedCards(drawnCards);
                            int addClaimCardsCount = chosenRoute.additionalClaimCardsCount(playerClaimCards, drawnCards);
                            // Affichage des cartes additionnelles tirées
                            receiveInfoForBothPlayers(players, information
                                    .drewAdditionalCards(drawnCards, addClaimCardsCount));
                            if (addClaimCardsCount == 0) {
                                // Ajout de la route et retrait des cartes initiales
                                gameState = gameState.withClaimedRoute(chosenRoute, playerClaimCards);
                                receiveInfoForBothPlayers(players, information.claimedRoute(chosenRoute, playerClaimCards));
                                AudioPlayer.play("/ziw.wav",false);
                            } else {
                                // Cartes que le joueur peut jouer
                                List<SortedBag<Card>> playableCards = gameState.currentPlayerState()
                                        .possibleAdditionalCards(addClaimCardsCount, playerClaimCards);

                                if (playableCards.isEmpty()) {
                                    receiveInfoForBothPlayers(players, information.didNotClaimRoute(chosenRoute));
                                } else {
                                    // Cartes que le joueur va jouer
                                    SortedBag<Card> playedAddCards = currentPlayer.chooseAdditionalCards(playableCards);
                                    if (playedAddCards.isEmpty()) {
                                        receiveInfoForBothPlayers(players, information.didNotClaimRoute(chosenRoute));
                                    } else {
                                        // Ajout de la route et retrait des cartes
                                        gameState = gameState.withClaimedRoute(chosenRoute, playedAddCards.union(playerClaimCards));
                                        receiveInfoForBothPlayers(players, information.claimedRoute(chosenRoute, playedAddCards.union(playerClaimCards)));
                                    }
                                }

                            }
                            break;
                    }
                    break;
            }
            // Condition de sortie de la boucle while
            if (gameState.currentPlayerId().equals(gameState.lastPlayer())) {
                break;
            }
            if (gameState.lastTurnBegins()) {
                int carCount = gameState.playerState(gameState.currentPlayerId()).carCount();
                receiveInfoForBothPlayers(players, playerInformation.get(gameState.currentPlayerId())
                        .lastTurnBegins(carCount));
            }
            gameState = gameState.forNextTurn();
        }

        //-------------------------------------- Fin de la partie ------------------------------------------------------

        // Informe les joueurs du résultat final de la partie
        updateStateForBothPlayers(players, gameState);
        // Initialisation:
        // Map des chemins les plus longs
        Map<PlayerId, Trail> playerLongestTrails = new EnumMap<>(PlayerId.class);
        // Map des points des joueurs
        Map<PlayerId, Integer> playerPoints = new EnumMap<>(PlayerId.class);
        for (Map.Entry<PlayerId, Player> entry : players.entrySet()) {
            playerLongestTrails.put(entry.getKey(), Trail.longest(gameState.playerState(entry.getKey()).routes()));
            playerPoints.put(entry.getKey(), gameState.playerState(entry.getKey()).finalPoints());
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
     * @param info    Type d'information transmis
     */
    private static void receiveInfoForBothPlayers(Map<PlayerId, Player> players, String info) {
        players.forEach((k, v) -> v.receiveInfo(info));
    }

    /**
     * Met à jour l'état de jeu pour les joueurs, en appelant la méthode updateState de chacun d'eux.
     *
     * @param players  Map des joueurs
     * @param newState Nouvel état de jeu
     */
    private static void updateStateForBothPlayers(Map<PlayerId, Player> players, GameState newState) {
        players.forEach((k, v) -> v.updateState(newState, newState.playerState(k)));
    }

    /**
     * Methode permettant de calculer le plus long chemin et d'attribuer les points bonus aux joueurs
     *
     * @param players             Map des joueurs
     * @param playerPoints        Map des points des joueurs
     * @param playerInformation   Map des informations des joueurs
     * @param playerLongestTrails Map des chemins les plus longs
     */
    private static void longestDeclaration(Map<PlayerId, Player> players,
                                           Map<PlayerId, Integer> playerPoints,
                                           Map<PlayerId, Info> playerInformation,
                                           Map<PlayerId, Trail> playerLongestTrails) {

        int conditionTrail = Integer.compare(playerLongestTrails.get(PlayerId.PLAYER_1).length(), playerLongestTrails.get(PlayerId.PLAYER_2).length());
        if (conditionTrail > 0) {
            // Joueur 1 a le bonus
            longestFinalDeclaration(players, playerPoints, playerInformation, playerLongestTrails, PlayerId.PLAYER_1);
        } else if (conditionTrail < 0) {
            // Joueur 2 a le bonus
            longestFinalDeclaration(players, playerPoints, playerInformation, playerLongestTrails, PlayerId.PLAYER_2);
        } else {
            // Meme longueur
            longestFinalDeclaration(players, playerPoints, playerInformation, playerLongestTrails, PlayerId.PLAYER_1);
            longestFinalDeclaration(players, playerPoints, playerInformation, playerLongestTrails, PlayerId.PLAYER_2);
        }
    }

    private static void longestFinalDeclaration(Map<PlayerId, Player> players,
                                                Map<PlayerId, Integer> playerPoints,
                                                Map<PlayerId, Info> playerInformation,
                                                Map<PlayerId, Trail> playerLongestTrails,
                                                PlayerId playerID) {
        receiveInfoForBothPlayers(players, playerInformation.get(playerID).getsLongestTrailBonus(playerLongestTrails.get(playerID)));
        playerPoints.replace(playerID, playerPoints.get(playerID) + LONGEST_TRAIL_BONUS_POINTS);

    }

    /**
     * Methode permettant d'afficher le joueur gagnant ou, s'il y a égalité
     *
     * @param players           Map des joueurs
     * @param playerNames       Map des noms des joueurs
     * @param playerPoints      Map des points des joueurs
     * @param playerInformation Map des informations des joueurs
     */
    private static void winnerDeclaration(Map<PlayerId, Player> players,
                                          Map<PlayerId, String> playerNames,
                                          Map<PlayerId, Integer> playerPoints,
                                          Map<PlayerId, Info> playerInformation) {

        ArrayList<String> playerName = new ArrayList<>(playerNames.values());
        int conditionPoint = Integer.compare(playerPoints.get(PlayerId.PLAYER_1), playerPoints.get(PlayerId.PLAYER_2));

        if (conditionPoint > 0) {
            // Joueur 1 gagne
            receiveInfoForBothPlayers(players, playerInformation.get(PlayerId.PLAYER_1).won(
                    playerPoints.get(PlayerId.PLAYER_1),
                    playerPoints.get(PlayerId.PLAYER_2)));
        } else if (conditionPoint < 0) {
            // Joueur 2 gagne
            receiveInfoForBothPlayers(players, playerInformation.get(PlayerId.PLAYER_2).won(
                    playerPoints.get(PlayerId.PLAYER_2),
                    playerPoints.get(PlayerId.PLAYER_1)));
        } else {
            // Egalité
            receiveInfoForBothPlayers(players, Info.draw(playerName, playerPoints.get(PlayerId.PLAYER_1)));
        }
    }

}
