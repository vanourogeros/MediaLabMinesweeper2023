package com.medialab.medialabminesweeper2023;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;

import static com.medialab.medialabminesweeper2023.Game.*;

public class ShowSolutionHandler implements EventHandler<ActionEvent> {
    @Override
    public void handle(ActionEvent actionEvent) {
        for(int i = 0; i < X_TILES; i++)
            for (int j = 0; j < Y_TILES; j++) {
                grid[i][j].Reveal();
            }

        Platform.runLater(() -> {
            executor.shutdown();
            GameResultFileHandler.saveGameResult(new GameResult("LOSE", time, tries));
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Game Over!");
            alert.setHeaderText("Boom bozo");
            GameOverWindow.show(alert, numBombs, superbomb, difficultyLevel, timeLimit, false);
        });


    }
}
