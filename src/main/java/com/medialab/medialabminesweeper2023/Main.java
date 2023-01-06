package com.medialab.medialabminesweeper2023;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class Main extends Application {

    static Scene scene;
    static Stage stage;
    static GridPane gridPane;
    Game game = new Game();
    final Menu menu1 = new Menu("Application");
    final Menu menu2 = new Menu("Details");

    static VBox vBox;
    MenuItem createOption = new MenuItem("Create");
    MenuItem loadOption = new MenuItem("Load");
    MenuItem startOption = new MenuItem("Start");
    MenuItem exitOption = new MenuItem("Exit");

    MenuItem roundsOption = new MenuItem("Rounds");
    MenuItem solutionOption = new MenuItem("Solution");



    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        gridPane = new GridPane();

        MenuBar menuBar = new MenuBar();
        menu1.getItems().addAll(createOption, loadOption, startOption, exitOption);
        menu2.getItems().addAll(roundsOption, solutionOption);
        menuBar.getMenus().addAll(menu1, menu2);

        vBox = new VBox(menuBar);
        gridPane.add(vBox, 0,0);
        gridPane.add(Game.createContent(), 0,1);


        createOption.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                // Create the dialog
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("Create Game");

                // Create the form elements
                TextField numBombsField = new TextField();
                CheckBox superbombCheckBox = new CheckBox("Superbomb");
                ToggleGroup difficultyGroup = new ToggleGroup();
                RadioButton easyButton = new RadioButton("Easy");
                easyButton.setToggleGroup(difficultyGroup);
                RadioButton hardButton = new RadioButton("Hard");
                hardButton.setToggleGroup(difficultyGroup);

                // Add the form elements to the dialog
                VBox content = new VBox(10);
                content.getChildren().addAll(
                        new Label("Number of Bombs:"), numBombsField,
                        superbombCheckBox,
                        new Label("Difficulty:"), easyButton, hardButton
                );
                dialog.getDialogPane().setContent(content);

                // Add buttons to the dialog
                ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().add(createButtonType);
                ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                dialog.getDialogPane().getButtonTypes().add(cancelButtonType);

                // Show the dialog and wait for the user's response
                Optional<ButtonType> result = dialog.showAndWait();

                // Process the user's response
                if (result.isPresent() && result.get() == createButtonType) {
                    // User clicked the Create button, get the form values
                    int numBombs = Integer.parseInt(numBombsField.getText());
                    boolean superbomb = superbombCheckBox.isSelected();
                    RadioButton selectedButton = (RadioButton) difficultyGroup.getSelectedToggle();
                    String difficulty = selectedButton.getText();
                    // ...
                }
            }
        });

        loadOption.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                // Create the file chooser
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open .txt File");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

                // Set the starting folder to the multimedia directory
                String currentDirectory = System.getProperty("user.dir");
                fileChooser.setInitialDirectory(new File(currentDirectory + "/multimedia"));
                // Show the file chooser and wait for the user to select a file
                File file = fileChooser.showOpenDialog(stage);

                if (file != null) {
                    System.out.println(file.length());
                }
            }
        });

        exitOption.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                // Terminate the app
                System.exit(0);
            }
        });

        startOption.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                // Start a new game
                Game.new_game();
            }
        });

        scene = new Scene(gridPane, 360, 385);

        stage.setTitle("MediaLab Minesweeper");
        stage.setScene(scene);
        stage.show();
    }



    public static void main(String[] args) {
        launch();
    }
}