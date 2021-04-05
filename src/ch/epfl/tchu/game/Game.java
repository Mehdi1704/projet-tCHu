package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.Map;
import java.util.Random;

public final class Game{
    public static final int PLAYERS_COUNT = 2;
    public static void play(Map<PlayerId, Player> players, Map<PlayerId,
                            String> playerNames, SortedBag<Ticket> tickets, Random rng){
        Preconditions.checkArgument(players.size()==PLAYERS_COUNT && playerNames.size()==PLAYERS_COUNT);
       // String p1Name = playerNames.get(PlayerId.PLAYER_1);
       // String p2Name = playerNames.get(PlayerId.PLAYER_2);
        var info1 = new Info(playerNames.get(PlayerId.PLAYER_1));
        var info2 = new Info(playerNames.get(PlayerId.PLAYER_2));
        Player p1 = players.get(PlayerId.PLAYER_1);
        Player p2 = players.get(PlayerId.PLAYER_2);
        p1.initPlayers(PlayerId.PLAYER_1,playerNames);
        p2.initPlayers(PlayerId.PLAYER_2,playerNames);
        //TODO tirer au hasard
        receiveInfoForBothPlayers(players,info1.willPlayFirst());

        p1.setInitialTicketChoice(tickets);
        p2.setInitialTicketChoice(tickets);

        p1.chooseInitialTickets();
        receiveInfoForBothPlayers(players, info1.keptTickets(1));
        p2.chooseInitialTickets();
        receiveInfoForBothPlayers(players, info2.keptTickets(1));


    }
    private static void receiveInfoForBothPlayers(Map<PlayerId, Player> players, String info){
        players.get(PlayerId.PLAYER_1).receiveInfo(info);
        players.get(PlayerId.PLAYER_2).receiveInfo(info);
    }
}
