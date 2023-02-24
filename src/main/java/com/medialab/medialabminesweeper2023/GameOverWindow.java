package com.medialab.medialabminesweeper2023;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class GameOverWindow {
    public static void show(Alert alert, int numBombs, int superbomb, int difficultyLevel, int timeLimit) {
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
