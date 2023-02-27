package com.medialab.medialabminesweeper2023;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.util.Objects;
import java.util.Optional;

/**
 * The Main class is the entry point for the MediaLab Minesweeper game application.
 * It defines the game's UI and event handlers.
 */
public class Main extends Application {

    static Scene scene;
    static Stage stage;
    static GridPane gridPane;
    final Menu menu1 = new Menu("Application");
    final Menu menu2 = new Menu("Details");

    static VBox vBox;
    MenuItem createOption = new MenuItem("Create");
    MenuItem loadOption = new MenuItem("Load");
    MenuItem startOption = new MenuItem("Start");
    MenuItem exitOption = new MenuItem("Exit");

    MenuItem roundsOption = new MenuItem("Rounds");
    MenuItem solutionOption = new MenuItem("Solution");

    Label timerLabel;
    Label bombLabel;
    Label markLabel;
    int time;
    Timeline timeline;
    int difficultyLevel=1, numBombs=10, timeLimit=150, superbomb=0; // default values if a file isn't provided.


    private void updateTimer() {
        timerLabel.setText(String.format("Time remaining: %d seconds", time));
    }

    /**
     * The start method is called by the JavaFX runtime when the game application is launched.
     * It initializes the game's UI and sets up event handlers.
     * @param stage the primary stage for the game application
     * @throws IOException if an IO error occurs
     */
    @Override
    public void start(Stage stage) throws IOException {
        Main.stage = stage;
        gridPane = new GridPane();

        MenuBar menuBar = new MenuBar();
        menu1.getItems().addAll(createOption, loadOption, startOption, exitOption);
        menu2.getItems().addAll(roundsOption, solutionOption);
        menuBar.getMenus().addAll(menu1, menu2);

        vBox = new VBox(menuBar);
        gridPane.add(vBox, 0,0, 3, 1);


        createOption.setOnAction(new CreateOptionHandler());

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

                    } catch (IOException e) {
                        // There was an error reading the file
                    } catch (NumberFormatException e) {
                        try {
                            difficultyLevel = 1;
                            numBombs = 10;
                            timeLimit = 150;
                            superbomb = 0;
                            throw new InvalidDescriptionException("Invalid file format");
                        } catch (InvalidDescriptionException ex) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Warning");
                            alert.setHeaderText(null);
                            alert.setContentText(ex.getErrorMessage());
                            alert.showAndWait();
                        }
                    } catch (InvalidValueException e) {
                        difficultyLevel = 1;
                        numBombs = 10;
                        timeLimit = 150;
                        superbomb = 0;
                        Alert alert = new Alert(Alert.AlertType.ERROR);
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
                try {
                    Game.new_game(numBombs, superbomb, difficultyLevel, timeLimit);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        roundsOption.setOnAction(new ShowRecordsHandler());

        solutionOption.setOnAction(new ShowSolutionHandler());

        scene = new Scene(gridPane, 400, 425);
        // Create a welcome screen
        Label welcomeLabel = new Label("Welcome to Minesweeper+!");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        welcomeLabel.setTextFill(Color.BLUE);
        GridPane.setHalignment(welcomeLabel, HPos.CENTER);
        GridPane.setValignment(welcomeLabel, VPos.CENTER);
        gridPane.add(welcomeLabel, 0, 1);

        Label subLabel = new Label("Click on Application>Load to load a new game or start a new\n game with default settings by clicking on Application>Start.");
        GridPane.setHalignment(subLabel, HPos.CENTER);
        GridPane.setValignment(subLabel, VPos.CENTER);
        gridPane.add(subLabel, 0, 2);

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(100);
        gridPane.getColumnConstraints().add(columnConstraints);
        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setPercentHeight(38);
        gridPane.getRowConstraints().add(rowConstraints);

        // Load the image
        String currentDir = System.getProperty("user.dir");
        Image image = new Image(currentDir + "/assets/boom.gif");

        // Create a background image
        BackgroundImage backgroundImage = new BackgroundImage(
                image,
                BackgroundRepeat.REPEAT, // Repeat the image
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT
        );

        // Create a background
        Background background = new Background(backgroundImage);

        // Set the background of the scene
        gridPane.setBackground(background);

        stage.setTitle("MediaLab Minesweeper");
        stage.setScene(scene);
        stage.show();

        File file = new File("./multimedia/game_results.dat");

        if (!file.exists()) {
            try {
                GameResultFileHandler.createFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * The main method that starts the JavaFX application.
     * @param args an array of command-line arguments for the application
     */
    public static void main(String[] args) {
        launch();
    }
}