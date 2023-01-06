package com.medialab.medialabminesweeper2023;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    static Scene scene;
    Game game = new Game();
    final Menu menu1 = new Menu("Application");
    final Menu menu2 = new Menu("Details");

    MenuItem m11 = new MenuItem("Create");
    MenuItem m12 = new MenuItem("Load");
    MenuItem m13 = new MenuItem("Start");
    MenuItem m14 = new MenuItem("Exit");

    MenuItem m21 = new MenuItem("Rounds");
    MenuItem m22 = new MenuItem("Solution");


    @Override
    public void start(Stage stage) throws IOException {

        GridPane gridPane = new GridPane();

        MenuBar menuBar = new MenuBar();
        menu1.getItems().addAll(m11, m12, m13, m14);
        menu2.getItems().addAll(m21, m22);
        menuBar.getMenus().addAll(menu1, menu2);

        VBox vBox = new VBox(menuBar);
        gridPane.add(vBox, 0,0);
        gridPane.add(Game.createContent(), 0,1);

        Scene scene = new Scene(gridPane, 960, 600);

        stage.setTitle("MediaLab Minesweeper");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}