package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

public class testwind extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        ObservableList<Text> infos = FXCollections.observableArrayList();


        List<SortedBag<Card>> listOfBags = List.of(SortedBag.of(1, Card.ORANGE, 3, Card.RED),
                SortedBag.of(3, Card.WHITE, 4, Card.LOCOMOTIVE),
                SortedBag.of(2, Card.LOCOMOTIVE, 1, Card.BLUE),
                SortedBag.of(1, Card.GREEN, 1, Card.BLACK));


        Map<PlayerId, String> playerNames =
                Map.of(PLAYER_1, "Ada", PLAYER_2, "Charles");
        GraphicalPlayer p = new GraphicalPlayer(PLAYER_1, playerNames);
        setState(p);

        //p.chooseTickets(SortedBag.of(ChMap.tickets().subList(0, 5)), (e) -> System.out.println("tickets choisis"));
        ActionHandlers.DrawTicketsHandler drawTicketsH =
                () -> p.chooseTickets(SortedBag.of(ChMap.tickets().subList(0,5)),(e) -> System.out.println("tickets choisis"));



        ActionHandlers.DrawCardHandler drawCardH =
                s -> p.receiveInfo(String.format("Je tire une carte de %s !", s));
        ActionHandlers.ClaimRouteHandler claimRouteH =
                (r, cs) -> {
                    String rn = r.station1() + " - " + r.station2();
                    p.receiveInfo(String.format("Je m'empare de %s avec %s", rn, cs));
                };

        p.startTurn(drawTicketsH, claimRouteH, drawCardH);
    }


    private void setState(GraphicalPlayer player) {
        PlayerState p1State =
                new PlayerState(SortedBag.of(ChMap.tickets().subList(0, 3)),
                        SortedBag.of(1, Card.WHITE, 3, Card.RED),
                        ChMap.routes().subList(0, 3));

        PublicPlayerState p2State =
                new PublicPlayerState(0, 0, ChMap.routes().subList(3, 6));

        Map<PlayerId, PublicPlayerState> pubPlayerStates =
                Map.of(PLAYER_1, p1State, PLAYER_2, p2State);
        PublicCardState cardState =
                new PublicCardState(Card.ALL.subList(0, 5), 110 - 2 * 4 - 5, 0);
        PublicGameState publicGameState =
                new PublicGameState(36, cardState, PLAYER_1, pubPlayerStates, null);
        player.setState(publicGameState, p1State);
    }
}
