package com.medialab.medialabminesweeper2023;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class Main extends Application {

    static Scene scene;
    static Stage stage;
    GridPane gridPane;
    final Menu menu1 = new Menu("Application");
    final Menu menu2 = new Menu("Details");

    static VBox vBox;
    MenuItem createOption = new MenuItem("Create");
    MenuItem loadOption = new MenuItem("Load");
    MenuItem startOption = new MenuItem("Start");
    MenuItem exitOption = new MenuItem("Exit");

    MenuItem roundsOption = new MenuItem("Rounds");
    MenuItem solutionOption = new MenuItem("Solution");

    static int TIME_LIMIT = 120;
    Label timerLabel;
    int time;
    Timeline timeline;
    int difficultyLevel, numBombs, timeLimit, superbomb;


    private void updateTimer() {
        timerLabel.setText(String.format("Time remaining: %d seconds", time));
    }

    @Override
    public void start(Stage stage) throws IOException {
        Main.stage = stage;
        gridPane = new GridPane();

        MenuBar menuBar = new MenuBar();
        menu1.getItems().addAll(createOption, loadOption, startOption, exitOption);
        menu2.getItems().addAll(roundsOption, solutionOption);
        menuBar.getMenus().addAll(menu1, menu2);

        vBox = new VBox(menuBar);
        gridPane.add(vBox, 0,0);


        createOption.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                // Create the dialog
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("Create Game");

                // Create the form elements
                TextField scenarioIDField = new TextField();
                TextField numBombsField = new TextField();
                TextField timeLimitField = new TextField();
                CheckBox superbombCheckBox = new CheckBox("Superbomb");
                ToggleGroup difficultyGroup = new ToggleGroup();
                RadioButton easyButton = new RadioButton("Easy");
                easyButton.setToggleGroup(difficultyGroup);
                RadioButton hardButton = new RadioButton("Hard");
                hardButton.setToggleGroup(difficultyGroup);

                // Add the form elements to the dialog
                VBox content = new VBox(10);
                content.getChildren().addAll(
                        new Label("Scenario ID:"), scenarioIDField,
                        new Label("Number of Bombs:"), numBombsField,
                        new Label("Time Limit:"), timeLimitField,
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
                    String scenarioID = scenarioIDField.getText();
                    int numBombs = Integer.parseInt(numBombsField.getText());
                    int timeLimit = Integer.parseInt(timeLimitField.getText());
                    int superbomb = superbombCheckBox.isSelected() ? 1 : 0;
                    RadioButton selectedButton = (RadioButton) difficultyGroup.getSelectedToggle();
                    int difficulty = Objects.equals(selectedButton.getText(), "Easy") ? 1 : 2;
                    // ...
                    GameWriter.writeConfigFile(scenarioID, numBombs, superbomb, difficulty, timeLimit);
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
                    // Parse file
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line = reader.readLine();
                        difficultyLevel = Integer.parseInt(line);
                        line = reader.readLine();
                        numBombs = Integer.parseInt(line);
                        line = reader.readLine();
                        timeLimit = Integer.parseInt(line);
                        line = reader.readLine();
                        superbomb = Integer.parseInt(line);

                        // Validate values
                        if (difficultyLevel == 1) {
                            if (numBombs < 9 || numBombs > 11)
                                throw new InvalidValueException("Number of bombs is out of bounds (For easy difficulty no. of bombs must be between 9 and 11).");
                            if (timeLimit < 120 || timeLimit > 180)
                                throw new InvalidValueException("Time limit is out of bounds (For easy difficulty time must be between 120 and 180).");
                            if (superbomb != 0)
                                throw new InvalidValueException("Superbomb value is invalid (Note that in easy mode" +
                                        " there cannot be a superbomb, thus its configuration value must be 0.)");
                        }
                        else if (difficultyLevel == 2) {
                            if (numBombs < 35 || numBombs > 45)
                                throw new InvalidValueException("Number of bombs is out of bounds (For hard difficulty no. of bombs must be between 35 and 45).");
                            if (timeLimit < 240 || timeLimit > 360)
                                throw new InvalidValueException("Time limit is out of bounds (For hard difficulty time must be between 240 and 360).");
                            if (superbomb != 0 && superbomb != 1)
                                throw new InvalidValueException("Superbomb value is invalid (Value must be 0 or 1).");
                        }
                        else {
                            throw new InvalidValueException("Difficulty level is invalid (Must be either 1 or 2).");
                        }



                        // TODO: Save values
                        // ...
                    } catch (IOException e) {
                        // There was an error reading the file
                    } catch (NumberFormatException e) {
                        // One of the values could not be parsed as an integer
                        try {
                            throw new InvalidDescriptionException("Invalid file format");
                        } catch (InvalidDescriptionException ex) {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Warning");
                            alert.setHeaderText(null);
                            alert.setContentText(ex.getErrorMessage());
                            alert.showAndWait();
                        }
                    } catch (InvalidValueException e) {
                        // One of the values could not be parsed as an integer
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Warning");
                        alert.setContentText(e.getErrorMessage());
                        alert.showAndWait();
                    }
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
                Game.new_game(numBombs, superbomb, difficultyLevel, timeLimit);
            }
        });

        scene = new Scene(gridPane, 400, 425);

        stage.setTitle("MediaLab Minesweeper");
        stage.setScene(scene);
        stage.show();
    }



    public static void main(String[] args) {
        launch();
    }
}