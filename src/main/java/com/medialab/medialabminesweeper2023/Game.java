package com.medialab.medialabminesweeper2023;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
    static int time = 150;
    static int tries;
    static Timeline timeline;
    static int difficultyLevel = 1;
    static int numBombs = 10;
    static int timeLimit;
    static int superbomb;
    boolean[] hasBomb;
    static int openTiles = 0;
    static int superbomb_x;
    static int superbomb_y;

    static ScheduledExecutorService executor;

    private static void updateTimer() {
        timerLabel.setText(String.format("Time: %d", time));
    }
    static void new_game(int numBombs, int superbomb, int difficulty, int timeLimit) throws FileNotFoundException {
        time = timeLimit;
        tries = 0;
        openTiles = 0;
        Game.timeLimit = timeLimit;
        Game.numBombs = numBombs;
        Game.difficultyLevel = difficulty;
        Game.superbomb = superbomb;


        GridPane gridPane = new GridPane();
        gridPane.add(Main.vBox, 0,0);
        timerLabel = new Label();
        timerLabel.setText(String.format("Time: %d", time));
        gridPane.add(timerLabel, 0, 1);
        gridPane.add(Game.createContent(numBombs, difficulty, superbomb), 0,2);
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
                        GameResultFileHandler.saveGameResult(new GameResult("LOSE", 0, tries));
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Game Over");
                        alert.setHeaderText("Time's Up!");
                        GameOverWindow.show(alert, numBombs, superbomb, difficultyLevel, timeLimit, false);
                    });
                }
            }, 0, 1, TimeUnit.SECONDS);
        }
    }

    static Parent createContent(int numBombs, int difficulty, int superbomb) throws FileNotFoundException {
        X_TILES = Y_TILES = difficulty == 2 ? 16 : 9; // Hard difficulty has 16x16 grid, Easy 9x9
        grid = new Tile[X_TILES][Y_TILES];
        Pane root = new Pane();
        root.setPrefSize(X_TILES * TILE_SIZE, Y_TILES * TILE_SIZE);

        Random random = new Random();
        boolean[][] bombs_map = new boolean[X_TILES][Y_TILES]; // Array with places with bombs
        List<int[]> bombList = new ArrayList<>(); // initialize bomb location list
        List<String> mineLines = new ArrayList<>();

        for (int i = 0; i < numBombs; i++) {
            int row, col;
            do {
                row = random.nextInt(Y_TILES); // random row index
                col = random.nextInt(X_TILES); // random column index
            } while (bombs_map[row][col] == true); // ensure no two bombs are placed at the same position

            bombs_map[col][row] = true;
            bombList.add(new int[]{row, col}); // append (row, col) to bomb list
        }

        int superbombIndex = random.nextInt(bombList.size());

        for (int i = 0; i < numBombs; i++) {
            int[] bomb_list_elem = bombList.get(i);
            String isSuperbomb;
            if (i == superbombIndex && difficulty == 2 && superbomb == 1) {
                isSuperbomb = "1";
                superbomb_x = bomb_list_elem[1];
                superbomb_y = bomb_list_elem[0];
            }
            else
             isSuperbomb = "0";
            mineLines.add(bomb_list_elem[0] + "," + bomb_list_elem[1] + "," + isSuperbomb);
        }

        // Write the mineLines to file
        try (PrintWriter writer = new PrintWriter("multimedia/mines.txt")) {
            for (String line : mineLines) {
                writer.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int y = 0; y < Y_TILES; y++) {
            for (int x = 0; x < X_TILES; x++) {
                Tile tile = new Tile(x, y, bombs_map[x][y]);

                grid[x][y] = tile;
                root.getChildren().add(tile);
            }
        }
        Font font = Font.loadFont(new FileInputStream(new File("./assets/PressStart2P-Regular.ttf")), 16);

        for (int y = 0; y < Y_TILES; y++) {
            for (int x = 0; x < X_TILES; x++) {
                Tile tile = grid[x][y];
                tile.text.setFont(font);

                if (tile.hasBomb) {
                    tile.text.setFill(Color.MAROON);
                    continue;
                }

                long bombs = getNeighbors(tile).stream().filter(t -> t.hasBomb).count();

                if (bombs > 0) {
                    tile.text.setText(String.valueOf(bombs));

                    switch ((int) bombs) {
                        case 1 -> tile.text.setFill(Color.BLUE);
                        case 2 -> tile.text.setFill(Color.GREEN);
                        case 3 -> tile.text.setFill(Color.RED);
                        default -> tile.text.setFill(Color.BLACK);
                    }

                }

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
                executor.shutdown();
                GameResultFileHandler.saveGameResult(new GameResult("LOSE", time, tries));
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Game Over!");
                alert.setHeaderText("Boom bozo");
                GameOverWindow.show(alert, numBombs, superbomb, difficultyLevel, timeLimit, false);
            });

            return;
        }

        tile.isOpen = true;
        openTiles++;
        tile.text.setVisible(true);
        tile.border.setFill(null);
        tile.getChildren().remove(tile.flagImage);
        if (tile.text.getText().isEmpty()) {
            getNeighbors(tile).forEach(Game::open);
        }
    }

    public static void mark(Tile tile) {
        if (tile.isMarked) {
            tile.isMarked = false;
            // Remove Image
            tile.getChildren().remove(tile.flagImage);
            return;
        }
        // Add flag Image
        String currentDir = System.getProperty("user.dir");
        tile.isMarked = true;
        Image flagImg = new Image(currentDir + "/assets/flag.png");
        tile.flagImage = new ImageView(flagImg);
        tile.flagImage.setFitWidth(tile.getWidth());
        tile.flagImage.setFitHeight(tile.getHeight());
        tile.getChildren().add(tile.flagImage);

        if (difficultyLevel == 2 && superbomb == 1) {
            if (tile.x == superbomb_x && tile.y == superbomb_y && tries <= 3) {
                System.out.println("found the superbomb!");
                for (int dx = 0; dx < X_TILES; dx++)
                    grid[dx][tile.y].Reveal();
                for (int dy = 0; dy < Y_TILES; dy++)
                    grid[tile.x][dy].Reveal();
            }
        }
    }


}
