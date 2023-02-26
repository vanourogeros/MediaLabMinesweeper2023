package com.medialab.medialabminesweeper2023;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.io.FileNotFoundException;
import java.util.Objects;

/**
 * This class represents the game over window that appears when the game ends.
 * It displays a message, a gif image (balloons or explosions), and two buttons for restarting or exiting the game.
 */
public class GameOverWindow {

    /**
     * Shows the game over window. The parameters provided are to
     * restart the game with the same game parameters. The isWin parameter
     * is for the image shown in the window.
     *
     * @param alert           The alert that will be used to show the game over message.
     * @param numBombs        The number of bombs in the game.
     * @param superbomb       The number of superbombs in the game.
     * @param difficultyLevel The difficulty level of the game.
     * @param timeLimit       The time limit of the game.
     * @param isWin           Whether the game ended in a win or a loss.
     */
    public static void show(Alert alert, int numBombs, int superbomb, int difficultyLevel, int timeLimit, boolean isWin) {
        // Create a Region for the background
        Region background = new Region();
        background.setPrefSize(400, 200);
        String image_path = isWin ? "/assets/balloons.gif" : "/assets/boom.gif";
        System.out.println(alert.getHeaderText());
        String currentDir = System.getProperty("user.dir");
        background.setBackground(new Background(new BackgroundImage(
                new Image(currentDir + image_path),
                BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
                BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));

        // Set the background to the alert's DialogPane
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setContent(background);
        alert.setContentText("Do you want to restart the game?");
        ButtonType restartButton = new ButtonType("Restart");
        ButtonType exitButton = new ButtonType("Exit");
        alert.getButtonTypes().setAll(restartButton, exitButton);
        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == restartButton) {
                try {
                    Game.new_game(numBombs, superbomb, difficultyLevel, timeLimit);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Main.stage.close();
            }
        });
    }
}
