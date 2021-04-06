package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static ch.epfl.tchu.game.Constants.*;
import static ch.epfl.tchu.game.Player.TurnKind.*;

public final class GameChonb{

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

        //p1.chooseInitialTickets();
        //p2.chooseInitialTickets();
        // receiveInfoForBothPlayers(players, playerInformation.get(gameState.currentPlayerId()).keptTickets(3));
        // receiveInfoForBothPlayers(players, playerInformation.get(gameState.()).keptTickets(3));

        //-------------------------- commencement de la partie ----------------------------

        Player currentPlayer = players.get(gameState.currentPlayerId());
        Info information = playerInformation.get(gameState.currentPlayerId());
        Player.TurnKind typeAction = currentPlayer.nextTurn();
        //
        switch(typeAction) {

            case DRAW_TICKETS :

                break ;
            case DRAW_CARDS :
                for (int i = 0 ; i < 2 ; i++){
                    int actualDrawSlot = currentPlayer.drawSlot();          // Slot que le joueur va tirer 2 fois
                    updateStateForBothPlayers(players, gameState);
                    if (FACE_UP_CARD_SLOTS.contains(actualDrawSlot)){          // Tire des face up cards
                        gameState = gameState.withCardsDeckRecreatedIfNeeded(rng)
                                .withDrawnFaceUpCard(currentPlayer.drawSlot());

                        Card pickedCard = gameState.cardState().faceUpCard(actualDrawSlot);
                        updateStateForBothPlayers(players, gameState);
                        receiveInfoForBothPlayers(players,information.drewVisibleCard(pickedCard));
                    }
                    else if (actualDrawSlot == Constants.DECK_SLOT){          // Tire du Deck
                        gameState = gameState.withCardsDeckRecreatedIfNeeded(rng)
                                .withBlindlyDrawnCard();

                        updateStateForBothPlayers(players, gameState);
                        receiveInfoForBothPlayers(players,information.drewBlindCard());
                    }
                }
                break ;
            case CLAIM_ROUTE :
                Route chosenRoute = currentPlayer.claimedRoute();
                // Cartes possibles à jouer
                List<SortedBag<Card>> listOfCards = gameState.playerState(gameState.currentPlayerId())
                                                                 .possibleClaimCards(chosenRoute);
                //TODO condition nécessaire?
                if (listOfCards.contains(currentPlayer.initialClaimCards())){   // Verifie si le joueur joue les bonnes cartes
                    if (chosenRoute.level().equals(Route.Level.OVERGROUND)){    // Route en surface

                        // Ajout de la route et retrait des cartes
                        //TODO wagons utilisés?
                        GameState newGameState =  gameState.withClaimedRoute(chosenRoute, currentPlayer.initialClaimCards());

                        // Affichage et mise à jour
                        //TODO update necessaire?
                        updateStateForBothPlayers(players, newGameState);
                        receiveInfoForBothPlayers(players,information
                                .claimedRoute(chosenRoute, currentPlayer.initialClaimCards()));

                    } else if (chosenRoute.level().equals(Route.Level.UNDERGROUND)){    // Route en tunnel

                        // Affichage pour le tunnel
                        receiveInfoForBothPlayers(players,information
                                .attemptsTunnelClaim(chosenRoute,currentPlayer.initialClaimCards()));

                        SortedBag<Card> playerClaimCards = currentPlayer.initialClaimCards();
                        SortedBag.Builder<Card> drawnCardsBuilder = new SortedBag.Builder<>();
                        for (int i = 0 ; i < ADDITIONAL_TUNNEL_CARDS ; i++){
                            drawnCardsBuilder.add(gameState.withCardsDeckRecreatedIfNeeded(rng).topCard());
                        }
                        SortedBag<Card> drawnCards = drawnCardsBuilder.build();

                        GameState newGameState = gameState.withoutTopCard().withoutTopCard().withoutTopCard().withMoreDiscardedCards(drawnCards);
                        int addClaimCardsCount = chosenRoute.additionalClaimCardsCount(playerClaimCards, drawnCards);
                        // Affichage des cartes additionnelles tirées
                        receiveInfoForBothPlayers(players,information
                                .drewAdditionalCards(drawnCards, addClaimCardsCount));
                        // Cartes que le joueur peut jouer
                        List<SortedBag<Card>> playableCards = gameState.currentPlayerState().possibleAdditionalCards(
                                                              addClaimCardsCount,playerClaimCards,drawnCards);
                        // Cartes que le joueur va jouer
                        SortedBag<Card> playedAddCards = currentPlayer.chooseAdditionalCards(playableCards);
                        if (playedAddCards.isEmpty()){    // Tentative échouée
                            //TODO joueur reprend ses cartes
                            receiveInfoForBothPlayers(players,information.didNotClaimRoute(chosenRoute));
                        }else{                            // Tentative réussie
                            //TODO joueur prend la route et perd ses cartes
                            receiveInfoForBothPlayers(players,information.claimedRoute(chosenRoute,playedAddCards.union(playerClaimCards)));
                        }


                    }
                }else{
                    //Tentative de s'emparer de la route échouée
                    receiveInfoForBothPlayers(players,information.didNotClaimRoute(chosenRoute));
                    }
                break ;

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
