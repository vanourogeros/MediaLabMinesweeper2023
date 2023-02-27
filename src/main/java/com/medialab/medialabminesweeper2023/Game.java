package com.medialab.medialabminesweeper2023;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.HPos;
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

/**
 * The Game class represents a Minesweeper game. It stores the state of the game, including the game board, the number
 * of bombs, the game timer, opened tiles, etc. It provides methods to start the game, reveal a cell, flag a cell, and check the game
 * status, and it can also trigger a game over event (when the user wins or loses the game).
 */
public class Game {
    static int X_TILES = 9;
    static int Y_TILES = 9;
    static Tile[][] grid = new Tile[X_TILES][Y_TILES];
    Scene scene;

    static Label timerLabel;
    static Label bombLabel;
    static Label markLabel;
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
    static int marks;

    static ScheduledExecutorService executor;

    private static void updateTimer() {
        timerLabel.setText(String.format("Time: %d", time));
    }
    static void updateMarks() {
        markLabel.setText(String.format("Marks: %d", marks));
    }

    /**
     * Starts a new game with the specified number of bombs, superbomb existence, difficulty level, and time limit.
     *
     * @param numBombs the number of bombs to be placed in the game
     * @param superbomb 0 if superbomb doesn't exist, 1 if exists
     * @param difficulty the difficulty level of the game (1 for easy, 2 for hard)
     * @param timeLimit the time limit for the game, in seconds
     * @throws FileNotFoundException if the file to save the game result cannot be found
     */
    static void new_game(int numBombs, int superbomb, int difficulty, int timeLimit) throws FileNotFoundException {
        time = timeLimit;
        marks = 0;
        tries = 0;
        openTiles = 0;
        Game.timeLimit = timeLimit;
        Game.numBombs = numBombs;
        Game.difficultyLevel = difficulty;
        Game.superbomb = superbomb;


        GridPane gridPane = new GridPane();
        gridPane.add(Main.vBox, 0,0);
        timerLabel = new Label();
        bombLabel = new Label(String.format("Bombs: %d", numBombs));
        markLabel = new Label("Marks: 0");
        timerLabel.setText(String.format("Time: %d", time));
        timerLabel.setStyle("-fx-border-color: slategrey; -fx-background-color: lightgrey; -fx-font-size: 16px; -fx-text-fill: red; -fx-background-radius: 7.5; -fx-border-radius: 7.5");
        bombLabel.setStyle("-fx-border-color: slategrey; -fx-background-color: lightgrey; -fx-font-size: 16px; -fx-text-fill: blue; -fx-background-radius: 7.5; -fx-border-radius: 7.5");
        markLabel.setStyle("-fx-border-color: slategrey; -fx-background-color: lightgrey; -fx-font-size: 16px; -fx-text-fill: red; -fx-background-radius: 7.5; -fx-border-radius: 7.5");
        gridPane.add(timerLabel, 0, 1);
        gridPane.add(bombLabel, 1, 1);
        gridPane.add(markLabel, 2, 1);
        int tiles = difficulty == 1 ? 9 : 16;
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPrefWidth(tiles * TILE_SIZE / 3.0);
        column1.setHgrow(Priority.ALWAYS);

        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPrefWidth(tiles * TILE_SIZE / 3.0);
        column2.setHgrow(Priority.ALWAYS);

        ColumnConstraints column3 = new ColumnConstraints();
        column3.setPrefWidth(tiles * TILE_SIZE / 3.0);
        column3.setHgrow(Priority.ALWAYS);

        gridPane.getColumnConstraints().addAll(column1, column2, column3);
        GridPane.setHalignment(timerLabel, HPos.CENTER);
        GridPane.setHalignment(bombLabel, HPos.CENTER);
        GridPane.setHalignment(markLabel, HPos.CENTER);
        gridPane.add(Game.createContent(numBombs, difficulty, superbomb), 0,2);
        Scene scene = new Scene(gridPane);
        Main.stage.setScene(scene);
        Main.stage.show();
        if (executor == null || executor.isShutdown()) {
            executor = Executors.newScheduledThreadPool(1);
            executor.scheduleAtFixedRate(() -> {
                time--;
                Platform.runLater(Game::updateTimer);
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

    /**
     * Creates the game board with the specified number of bombs, difficulty level, and super-bomb (if exists).
     * Returns a Parent object that contains the game board.
     *
     * @param numBombs the number of bombs to be placed on the board
     * @param difficulty the difficulty level of the game (1 for easy, 2 for hard)
     * @param superbomb super-bomb to be used (0 for disabled, 1 for enabled)
     * @return a Parent object that contains the game board
     * @throws FileNotFoundException if the font file for the game is not found
     */

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

    /**
     * Returns a list of the neighboring tiles for the given tile.
     *
     * @param tile the tile for which to find neighbors
     * @return a list of neighboring tiles
     */
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

    /**
     * Reveals the tile and recursively reveals all neighboring tiles with no adjacent bombs.
     * If the tile contains a bomb, ends the game and displays the Game Over window.
     *
     * @param tile the tile to open
     */
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

        tile.Reveal();
        if (tile.text.getText().isEmpty()) {
            getNeighbors(tile).forEach(Game::open);
        }
    }

    /**
     * Marks the given tile with a red flag, or un-marks it
     * if it already has a flag. If a super-bomb (for hard difficulty) is marked
     * within the first 4 tries, all the tiles in the same
     * row and column are safely revealed.
     *
     * @param tile the tile to be marked (or unmarked)
     */
    public static void mark(Tile tile) {
        if (tile.isMarked) {
            marks--;
            tile.isMarked = false;
            // Remove Image
            tile.getChildren().remove(tile.flagImage);
            updateMarks();
            return;
        }
        // Add flag Image
        String currentDir = System.getProperty("user.dir");
        marks++;
        tile.isMarked = true;
        Image flagImg = new Image(currentDir + "/assets/flag.png");
        tile.flagImage = new ImageView(flagImg);
        tile.flagImage.setFitWidth(tile.getWidth());
        tile.flagImage.setFitHeight(tile.getHeight());
        tile.getChildren().add(tile.flagImage);

        if (difficultyLevel == 2 && superbomb == 1) {
            if (tile.x == superbomb_x && tile.y == superbomb_y && tries <= 4) {
                System.out.println("found the superbomb!");
                for (int dx = 0; dx < X_TILES; dx++)
                    grid[dx][tile.y].Reveal();
                for (int dy = 0; dy < Y_TILES; dy++)
                    grid[tile.x][dy].Reveal();
            }
        }
        updateMarks();
    }


}
