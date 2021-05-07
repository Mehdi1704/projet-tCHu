package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Map;

class InfoViewCreator {
    public static Node createInfoView(PlayerId playerId,
                                      Map<PlayerId, String> playerNames,
                                      ObservableGameState observableGameState,
                                      ListView<Text> textList){
        TextFlow textFlow = new TextFlow();
        textFlow.setId("game-info");
        //TODO liste de textes



        return null;
    }
}
