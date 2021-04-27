package ch.epfl.tchu.gui;

import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.awt.*;

public class circles extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ObjectProperty<String> p = new SimpleObjectProperty<>();
        p.addListener((o, oV, nV) -> System.out.println(nV));

        p.set("hello");                 // affiche "hello"
        p.set("world");                 // affiche "world"


    }
}
