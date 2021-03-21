package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;
import ch.epfl.tchu.game.Route.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainClass {
    public static void main (String[] args){

        final Station BAD = new Station(0, "Baden");
        final Station BAL = new Station(1, "Bâle");
        final Station BEL = new Station(2, "Bellinzone");
        final Station BER = new Station(3, "Berne");
        final Station BRI = new Station(4, "Brigue");
        final Station DEL = new Station(5, "Delémont");
        final Station FRI = new Station(6, "Fribourg");
        final Station LOC = new Station(7, "Locarno");

        final List<Route> ALL_ROUTES = List.of(
                new Route("BAD_BAL_1", BAD, BAL, 3, Level.UNDERGROUND, Color.RED),
                new Route("BAL_DEL_1", BAL, DEL, 2, Level.UNDERGROUND, Color.YELLOW),
                new Route("BEL_LOC_1", BEL, LOC, 1, Level.UNDERGROUND, Color.BLACK));
/*
        StationPartition.Builder tabTest = new StationPartition.Builder(5);
        //temp 2 vers temp 1
        tabTest.connect(BAL,BAD);//0 vers 1
        tabTest.connect(BAD,BER);//3 vers 0 sauf que 0 est vers 1 du coup 3 vers 1
        tabTest.connect(BRI,BAL);//bah c ca qui manque du coup we il est cense relier le tout avec
        //tabTest.connect(FRI,BRI);
        //ca a marche pour les autres psq on a fait le chemin inverse
        StationPartition b = tabTest.build();
        for (int i=0 ; i < b.getLinks().length ; ++i){
            System.out.println(b.getLinks()[i]);
        }

        System.out.println(PlayerState.possibleClaimCards(new Route("BEL_LOC_1", BEL, LOC, 3, Level.OVERGROUND, null)));
        System.out.println(PlayerState.possibleClaimCards(new Route("BEL_LOC_1", BEL, LOC, 3, Level.OVERGROUND, Color.BLUE)));
        System.out.println(PlayerState.possibleClaimCards(new Route("BEL_LOC_1", BEL, LOC, 3, Level.UNDERGROUND, Color.BLUE)));
*/

    }
}
