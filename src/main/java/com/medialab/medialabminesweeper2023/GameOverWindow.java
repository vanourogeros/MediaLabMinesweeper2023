package com.medialab.medialabminesweeper2023;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.util.Objects;

public class GameOverWindow {
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
                Game.new_game(numBombs, superbomb, difficultyLevel, timeLimit);
            } else {
                Main.stage.close();
            }
        });
    }
}
