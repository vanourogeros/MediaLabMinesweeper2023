package com.medialab.medialabminesweeper2023;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

import static com.medialab.medialabminesweeper2023.Tile.TILE_SIZE;

public class Game {
    static int X_TILES = 9;
    static int Y_TILES = 9;
    static Tile[][] grid = new Tile[X_TILES][Y_TILES];
    Scene scene;

    public Game() {

    }

    static Parent createContent() {
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
            Main.scene.setRoot(createContent());
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
