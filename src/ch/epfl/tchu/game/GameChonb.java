package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import static ch.epfl.tchu.game.Constants.INITIAL_TICKETS_COUNT;

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
