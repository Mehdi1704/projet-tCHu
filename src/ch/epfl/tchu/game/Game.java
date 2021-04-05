package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

public final class Game{

    public static final int PLAYERS_COUNT = 2;



    public static void play(Map<PlayerId, Player> players, Map<PlayerId,
                            String> playerNames, SortedBag<Ticket> tickets, Random rng){
        Preconditions.checkArgument(players.size()==PLAYERS_COUNT && playerNames.size()==PLAYERS_COUNT);

        Map<PlayerId,Info> playerInformation = new EnumMap<>(PlayerId.class);
        GameState gameState = GameState.initial(tickets,rng);

        playerNames.forEach((player,info) -> playerInformation.put(player, new Info(info)));
/*
        var info1 = new Info(playerNames.get(PlayerId.PLAYER_1));
        var info2 = new Info(playerNames.get(PlayerId.PLAYER_2));
*/
        players.forEach((key,value) -> value.initPlayers(key,playerNames));

        receiveInfoForBothPlayers(players,playerInformation.get(gameState.currentPlayerId()).willPlayFirst());

        //TODO lambda

        p1.setInitialTicketChoice(tickets);
        p2.setInitialTicketChoice(tickets);

        p1.chooseInitialTickets();
        p2.chooseInitialTickets();



        receiveInfoForBothPlayers(players, playerInformation.get(gameState.currentPlayerId()).keptTickets(3));
       // receiveInfoForBothPlayers(players, playerInformation.get(gameState.()).keptTickets(3));






    }
    private static void receiveInfoForBothPlayers(Map<PlayerId, Player> players, String info){
        players.get(PlayerId.PLAYER_1).receiveInfo(info);
        players.get(PlayerId.PLAYER_2).receiveInfo(info);
    }

    /**
     * méthode permettant d'informer tous les joueurs d'un changement d'état,
     * en appelant la méthode updateState de chacun d'eux.
     * @param players
     * @param newState
     * @param ownState
     */
    private static void updateStateForBothPlayers(Map<PlayerId, Player> players,PublicGameState newState,
                                                  PlayerState ownState){
        players.get(PlayerId.PLAYER_1).updateState(newState,ownState);
        players.get(PlayerId.PLAYER_2).updateState(newState,ownState);

    }

}
