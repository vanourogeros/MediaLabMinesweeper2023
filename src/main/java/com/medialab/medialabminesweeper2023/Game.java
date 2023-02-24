package com.medialab.medialabminesweeper2023;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.medialab.medialabminesweeper2023.Tile.TILE_SIZE;
import com.medialab.medialabminesweeper2023.GameOverWindow;

public class Game {
    static int X_TILES = 9;
    static int Y_TILES = 9;
    static Tile[][] grid = new Tile[X_TILES][Y_TILES];
    Scene scene;

    static Label timerLabel;
    static int time;
    static Timeline timeline;
    static int difficultyLevel;
    static int numBombs;
    static int timeLimit;
    static int superbomb;
    boolean[] hasBomb;

    static ScheduledExecutorService executor;

    private static void updateTimer() {
        timerLabel.setText(String.format("Time: %d", time));
    }
    static void new_game(int numBombs, int superbomb, int difficulty, int timeLimit) {
        time = timeLimit;
        Game.timeLimit = timeLimit;
        Game.numBombs = numBombs;
        Game.difficultyLevel = difficulty;
        Game.superbomb = superbomb;


        GridPane gridPane = new GridPane();
        gridPane.add(Main.vBox, 0,0);
        timerLabel = new Label();
        timerLabel.setText(String.format("Time: %d", time));
        gridPane.add(timerLabel, 0, 1);
        gridPane.add(Game.createContent(numBombs), 0,2);
        Scene scene = new Scene(gridPane);
        Main.stage.setScene(scene);
        Main.stage.show();
        if (executor == null || executor.isShutdown()) {
            executor = Executors.newScheduledThreadPool(1);
            executor.scheduleAtFixedRate(() -> {
                time--;
                Platform.runLater(() -> updateTimer());
                if (time == 0) {
                    executor.shutdown();
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Game Over");
                        alert.setHeaderText("Time's Up!");
                        GameOverWindow.show(alert, numBombs, superbomb, difficultyLevel, timeLimit);
                    });
                }
            }, 0, 1, TimeUnit.SECONDS);
        }
    }

    static Parent createContent(int numBombs) {
        Pane root = new Pane();
        root.setPrefSize(X_TILES * TILE_SIZE, Y_TILES * TILE_SIZE);

        for (int y = 0; y < Y_TILES; y++) {
            for (int x = 0; x < X_TILES; x++) {
                Tile tile = new Tile(x, y, Math.random() < 0.2);

                grid[x][y] = tile;
                root.getChildren().add(tile);
            }
        }

        for (int y = 0; y < Y_TILES; y++) {
            for (int x = 0; x < X_TILES; x++) {
                Tile tile = grid[x][y];

                if (tile.hasBomb) continue;

                long bombs = getNeighbors(tile).stream().filter(t -> t.hasBomb).count();

                if (bombs > 0)
                    tile.text.setText(String.valueOf(bombs));
            }
        }

        return root;
    }

    static List<Tile> getNeighbors(Tile tile) {
        List<Tile> neighbors = new ArrayList<>();

        int[] positions = new int[] { // positions of neighboring tiles
                -1, -1,
                -1, 0,
                -1, 1,
                0, -1,
                0, 1,
                1, -1,
                1, 0,
                1, 1,
        };

        for (int i = 0; i < positions.length; i+=2) {
            int dx = positions[i];
            int dy = positions[i+1];

            int newX = tile.x + dx;
            int newY = tile.y + dy;

            if (newX >= 0 && newX < X_TILES
                    && newY >= 0 && newY < Y_TILES) {
                neighbors.add(grid[newX][newY]);
            }
        }

        return neighbors;
    }

    public static void open(Tile tile) {
        if (tile.isOpen) return;

        if (tile.hasBomb) {
            System.out.println("Game Over lmao");

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Game Over");
                alert.setHeaderText("Boom bozo");
                GameOverWindow.show(alert, numBombs, superbomb, difficultyLevel, timeLimit);
            });

            return;
        }

        tile.isOpen = true;
        tile.text.setVisible(true);
        tile.border.setFill(null);

        if (tile.text.getText().isEmpty()) {
            getNeighbors(tile).forEach(Game::open);
        }
    }


}
