package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.net.RemotePlayerClient;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;


import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static javafx.scene.paint.Color.rgb;

public class MenuMain extends Application {

    private static int wagonsCount = 40;
    private static int initCardsCount = 4;
    private static int longestCount = 10;
    private static Color player1Color;
    private static Color player2Color;

    public static void main(String[] args) {
        Application.launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("TCHU GAME");
        StackPane layout = new StackPane();
        layout.getChildren().add(createMenu(primaryStage));
        Scene scene = new Scene(layout, 1120, 530);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static Node createMenu(Stage primaryStage) {
        BackgroundImage myBI = new BackgroundImage(new Image("/menu.png"),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        Pane paneFond = new Pane();
        paneFond.setBackground(new Background(myBI));
        Button serverButton = createButton("Héberger une partie", 100);
        Button clientButton = createButton("Rejoindre une partie", 400);
        Button quitButton = createButton("Quitter", 700);
        TextField ipField = new TextField();
        ipField.setPrefColumnCount(15);
        ipField.setPromptText("Adresse IP de l'hôte");
        ipField.setLayoutX(400);
        ipField.setLayoutY(460);
        paneFond.getChildren().addAll(serverButton, clientButton, ipField, quitButton);
        serverButton.setOnAction(h -> {
            paneFond.getChildren().clear();
            paneFond.getChildren().add(createServerPage(primaryStage));
        });
        clientButton.setOnAction(h -> {
            System.out.println("Client lancé");
            String address = ipField.getText();
            if (address.isEmpty()/*||!address.subSequence(0,8).equals("128.179.")*/) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("Veuillez entrer une adresse IP valide!");
                alert.show();
            } else {
                //Platform.setImplicitExit(false);
                try {
                    //setThemeColors(getPlayer1Color(), getPlayer2Color());
                    List<String> args = new ArrayList<>();
                    args.add(address);
                    args.add("5108");
                    launchClient(args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        quitButton.setOnAction(h -> primaryStage.close());
        return paneFond;
    }

    public static Node createServerPage(Stage primaryStage) {

        Pane paneFond = new Pane();
        Slider wagonsSlider = createSlider(20, 40, 5, 4);
        Slider initCardsSlider = createSlider(4, 12, 1, 0);
        Slider bonusLongestSlider = createSlider(5, 20, 1, 0);
        Button back = createButton("Retour", 100);
        Button launch = createButton("Lancer le jeu", 400);
        Text text1 = createText("Wagons initiaux");
        Text text2 = createText("Cartes initiales");
        Text text3 = createText("Bonus du plus long chemin");
        Text text4 = createText("Couleur de l'hôte");
        Text text5 = createText("Couleur de l'adversaire");
        Text text6 = createText("Nom de l'hôte");
        Text text7 = createText("Nom de l'adversaire");
        Text text8 = createText("Reglages de jeu:");

        TextField textField1 = new TextField();
        TextField textField2 = new TextField();

        ColorPicker colorPicker1 = new ColorPicker(rgb(173, 216, 230));
        ColorPicker colorPicker2 = new ColorPicker(rgb(255, 182, 193));

        launch.setOnAction(h -> {
            if (textField1.getText().equals("") || textField2.getText().equals("")) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("Veuillez entrer des noms pour les deux joueurs!");
                alert.show();

            } else {
                wagonsCount = (int) wagonsSlider.getValue();
                initCardsCount = (int) initCardsSlider.getValue();
                longestCount = (int) bonusLongestSlider.getValue();
                player1Color = colorPicker1.getValue();
                player2Color = colorPicker2.getValue();
                System.out.println("Serveur lancé");
                setThemeColors();
                //Platform.setImplicitExit(false);
                try {
                    List<String> args = new ArrayList<>();
                    args.add(textField1.getText());
                    args.add(textField2.getText());
                    launchServer(args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        VBox rulesBox = createVBox(10, 10);
        rulesBox.setStyle("-fx-background-color: #ffffffdd;" + "-fx-border-color: #000000;");
        rulesBox.getChildren().addAll(text8, text1, wagonsSlider, text2, initCardsSlider, text3, bonusLongestSlider);
        rulesBox.setMinSize(625, 325);

        VBox colorBox = createVBox(100, 200);
        colorBox.getChildren().addAll(text4, colorPicker1, text5, colorPicker2);

        VBox namesBox = createVBox(400, 200);
        namesBox.getChildren().addAll(text6, textField1, text7, textField2);

        paneFond.getChildren().addAll(rulesBox, launch, back, colorBox, namesBox);

        back.setOnAction(h -> {
            paneFond.getChildren().clear();
            paneFond.getChildren().add(createMenu(primaryStage));
        });

        return paneFond;
    }

    public static int getWagonsCount() {
        return wagonsCount;
    }

    public static int getInitCardsCount() {
        return initCardsCount;
    }

    public static int getLongestCount() {
        return longestCount;
    }

    public static Color getPlayer1Color() {
        return player1Color;
    }

    public static Color getPlayer2Color() {
        return player2Color;
    }

    public static void setConstants(List<String> listString) {
        System.out.println(listString);
        wagonsCount = Integer.parseInt(listString.get(0));
        initCardsCount = Integer.parseInt(listString.get(1));
        longestCount = Integer.parseInt(listString.get(2));
    }

    public static void setColors(List<String> listString) {
        player1Color = Color.web(listString.get(0));
        player2Color = Color.web(listString.get(1));
        setThemeColors();
    }

    public static void launchServer(List<String> args) throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(5108)) {
            Socket socket = serverSocket.accept();
            Map<PlayerId, String> playerNames =
                    Map.of(PLAYER_1, args.get(0), PLAYER_2, args.get(1));
            BufferedReader r = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), US_ASCII));
            BufferedWriter w = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(), US_ASCII));
            Player graphicalPlayer = new GraphicalPlayerAdapter();
            Player remotePlayerProxy = new RemotePlayerProxy(socket, r, w);
            Map<PlayerId, Player> players =
                    Map.of(PLAYER_1, graphicalPlayer, PLAYER_2, remotePlayerProxy);
            new Thread(() -> Game.play(players, playerNames, SortedBag.of(ChMap.tickets()), new Random())).start();
        }
    }

    public static void launchClient(List<String> args) throws Exception {
        GraphicalPlayerAdapter player = new GraphicalPlayerAdapter();
        RemotePlayerClient client = new RemotePlayerClient(player, args.get(0), Integer.parseInt(args.get(1)));
        new Thread(client::run).start();
    }

    private static Slider createSlider(int min, int max, int majTick, int minTick) {
        Slider slider = new Slider(min, max, 0);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.valueProperty().addListener(
                (obs, oldVal, newVal) -> slider.setValue(newVal.intValue()));
        slider.setMajorTickUnit(majTick);
        slider.setMinorTickCount(minTick);
        return slider;
    }

    private static Button createButton(String text, int x) {
        Button button = new Button(text);
        button.setStyle("-fx-font-size: 15pt;" + "-fx-background-color: #ffffffdd");
        button.setLayoutX(x);
        button.setLayoutY(350);
        button.setMinSize(200, 100);
        return button;
    }

    private static Text createText(String string) {
        Text text = new Text();
        text.setText(string);
        text.setFont(Font.font("Trebuchet MS", FontWeight.LIGHT, 15));
        return text;
    }

    private static VBox createVBox(int x, int y) {
        VBox vBox = new VBox();
        vBox.setLayoutX(x);
        vBox.setLayoutY(y);
        return vBox;
    }

    private static void setThemeColors() {
        try {
            File file = new File("resources/players.css");
            Path path = file.toPath();

            if (!file.exists()) {
                file.createNewFile();
            }

            Files.writeString(path,
                    ".PLAYER_1 .filled { -fx-fill: \"#" + getPlayer1Color().toString().subSequence(2, 8) + "\"; }\n" +
                            ".PLAYER_2 .filled { -fx-fill: \"#" + getPlayer2Color().toString().subSequence(2, 8) + "\"; }");
            path.toFile().deleteOnExit();

            System.out.println("Wrote " + path);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


