package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.net.RemotePlayerClient;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;
import static java.nio.charset.StandardCharsets.US_ASCII;

public class MenuMain extends Application {
    private static boolean launchGame;
    private static int wagonsCount;
    private static int initCardsCount;
    private static int longestCount;

    public static void main(String[] args){
        Application.launch(args);
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("TCHU GAME");
        StackPane layout = new StackPane();
        layout.getChildren().add(createMenu());
        if (launchGame){
            primaryStage.close();
            try {
                launchServer(Collections.singletonList("Ada"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Scene scene = new Scene(layout, 1000, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static Node createMenu(){

        BackgroundImage myBI = new BackgroundImage(new Image("/menu.png"),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        Pane paneFond = new Pane();
        paneFond.setBackground(new Background(myBI));
        Button serverButton = createButton("Héberger une partie",500, 200, 200, 100);
        Button clientButton = createButton("Rejoindre une partie", 500, 400, 200, 100);
        paneFond.getChildren().addAll(serverButton, clientButton);
        serverButton.setOnAction(h -> {
            paneFond.getChildren().clear();
            paneFond.getChildren().add(createServerPage());
        });
        return paneFond;
    }

    public static Node createServerPage(){

        Pane paneFond = new Pane();
        Slider wagonsSlider = createSlider(20, 40, 5);
        Slider initCardsSlider = createSlider(4, 12, 2);
        Slider bonusLongestSlider = createSlider(5, 20, 2);
        Button back = createButton("Retour", 200, 300, 100, 100);
        Button launch = createButton("Lancer le jeu", 600, 300, 100, 100);
        Text text1 = createText("Wagons initiaux");
        Text text2 = createText("Cartes initiales");
        Text text3 = createText("Bonus du plus long chemin");
        Text text4 = createText("Couleur de l'hôte");
        Text text5 = createText("Couleur de l'adversaire");

        ColorPicker colorPicker1 = new ColorPicker();
        ColorPicker colorPicker2 = new ColorPicker();

        Color value1 = colorPicker1.getValue();
        Color value2 = colorPicker1.getValue();



        launch.setOnAction(h -> {

            wagonsCount = (int)wagonsSlider.getValue();
            initCardsCount = (int)initCardsSlider.getValue();
            longestCount = (int)bonusLongestSlider.getValue();

            System.out.println((int)wagonsSlider.getValue());
            System.out.println((int)initCardsSlider.getValue());
            System.out.println((int)bonusLongestSlider.getValue());
            launchGame = true;
        });

        VBox rulesBox = new VBox();
        rulesBox.setStyle("-fx-background-color: #ffffff;" + "-fx-border-color: #000000;");
        rulesBox.getChildren().addAll(text1, wagonsSlider, text2, initCardsSlider, text3, bonusLongestSlider);
        rulesBox.setMinSize(700, 300);

        VBox colorBox = new VBox(text4, colorPicker1, text5, colorPicker2);
        colorBox.setLayoutX(50);
        colorBox.setLayoutY(200);

        paneFond.getChildren().addAll(rulesBox, launch, back,colorBox);

        back.setOnAction(h -> {
            paneFond.getChildren().clear();
            paneFond.getChildren().add(createMenu());
        });


        return paneFond;
    }

    public static int getWagonsCount(){
        return wagonsCount;
    }

    public static int getInitCardsCount() {
        return initCardsCount;
    }

    public static int getLongestCount() {
        return longestCount;
    }

    public static void launchServer(List<String> args) throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(5108)) {

            Socket socket = serverSocket.accept();

            Map<PlayerId, String> playerNames;

            if (args.isEmpty()) {
                playerNames = Map.of(PLAYER_1, "Ada", PLAYER_2, "Charles");
            } else if (args.size() == 1) {
                playerNames = Map.of(PLAYER_1, args.get(0), PLAYER_2, "Charles");
            } else {
                playerNames = Map.of(PLAYER_1, args.get(0), PLAYER_2, args.get(1));
            }

            BufferedReader r = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), US_ASCII));
            BufferedWriter w = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(), US_ASCII));

            Player graphicalPlayer = new GraphicalPlayerAdapter();
            Player remotePlayerProxy = new RemotePlayerProxy(socket, r, w);

            Map<PlayerId, Player> players =
                    Map.of(PLAYER_1, graphicalPlayer,
                            PLAYER_2, remotePlayerProxy);
            AudioPlayer.play("/mine.wav", true);
            new Thread(() -> Game.play(players, playerNames, SortedBag.of(ChMap.tickets()), new Random())).start();
        }
    }

    public static void launchClient(List<String> args) throws Exception {

        if(args.isEmpty()){
            args.add("localhost");
            args.add("5108");
        }else if(args.size()==1){
            args.add("5108");
        }

        GraphicalPlayerAdapter player = new GraphicalPlayerAdapter();
        RemotePlayerClient client = new RemotePlayerClient(player, args.get(0), Integer.parseInt(args.get(1)));
        new Thread(client::run).start();
    }

    private static Slider createSlider(int min, int max, int majTick){
        Slider slider = new Slider(min, max, 0);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.valueProperty().addListener(
                (obs, oldVal, newVal) -> slider.setValue(newVal.intValue()));
        slider.setMajorTickUnit(majTick);
        slider.setMinorTickCount(1);
        return slider;
    }

    private static Button createButton(String text, int x, int y, int width, int height){
        Button button = new Button(text);
        button.setLayoutX(x);
        button.setLayoutY(y);
        button.setMinSize(width, height);
        return button;
    }

    private static Text createText(String string){
        Text text = new Text();
        text.setText(string);
        return text;
    }
}
