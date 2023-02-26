package com.medialab.medialabminesweeper2023;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;

import static com.medialab.medialabminesweeper2023.Game.*;

/**
 * An event handler for the "Solution" menu Details option button. Reveals all tiles on the game grid and saves the game result as a loss.
 * Displays a game over message.
 */
public class ShowSolutionHandler implements EventHandler<ActionEvent> {

    /**
     * Handles the event when the "Solution" button is clicked.
     * Reveals all tiles on the game grid and saves the game result as a loss.
     * Displays a game over message.
     *
     * @param actionEvent The action event that triggered this handler (i.e. the Solution button click).
     */
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
