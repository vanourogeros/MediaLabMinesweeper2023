package com.medialab.medialabminesweeper2023;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import static com.medialab.medialabminesweeper2023.Game.*;

/**
 * A Tile represents a single square on the Minesweeper game board.
 * Each Tile can be either open or closed, marked with a flag or not, and may or may not contain a bomb.
 */
public class Tile extends StackPane {
    static int TILE_SIZE = 40;
    int x, y;
    boolean hasBomb;
    boolean isOpen = false;
    boolean isMarked = false;
    ImageView flagImage;
    Rectangle border = new Rectangle(TILE_SIZE - 2, TILE_SIZE - 2);
    Text text = new Text();

    /**
     * Constructs a new Tile object.
     * @param x The x-coordinate of the tile on the game board (between 0 and X_TILES-1).
     * @param y The y-coordinate of the tile on the game board (between 0 and X_TILES-1).
     * @param hasBomb Whether the tile contains a bomb or not.
     */
    public Tile(int x, int y, boolean hasBomb){
        this.x = x;
        this.y = y;
        this.hasBomb = hasBomb;

        border.setStroke(Color.LIGHTGRAY);
        this.border.setFill(new LinearGradient(0, 0, 1, 1, true, null,
                new Stop(0, Color.LIGHTGRAY),
                new Stop(0.4, Color.SLATEGRAY),
                new Stop(1, Color.SLATEBLUE)));

        text.setFont(Font.font(18));
        text.setText(hasBomb ? "X" : "");
        text.setVisible(false);

        // Add a drop shadow effect
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.5));
        dropShadow.setOffsetX(2);
        dropShadow.setOffsetY(2);
        dropShadow.setRadius(2);
        this.setEffect(dropShadow);

        getChildren().addAll(border, text);

        setTranslateX(x * TILE_SIZE);
        setTranslateY(y * TILE_SIZE);

        setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                if (!this.isOpen) Game.tries++;
                Game.open(this);

                // Check for win Condition upon tapping a tile
                if (Game.openTiles == X_TILES * Y_TILES - numBombs) {
                    Game.executor.shutdown();
                    System.out.println("You win!");
                    Platform.runLater(() -> {
                        // Win window
                        GameResultFileHandler.saveGameResult(new GameResult("WIN", time, tries));
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Congrats bozo");
                        alert.setHeaderText("You win!");
                        GameOverWindow.show(alert, numBombs, superbomb, difficultyLevel, timeLimit, true);
                    });
                }
            } else if (e.getButton() == MouseButton.SECONDARY) {
                if (!this.isOpen) // Can only mark closed tiles
                    Game.mark(this);
            }
        });
    }

    /**
     * Reveals the contents of the tile.
     */
    void Reveal() {
        if (this.isOpen) return;
        this.isOpen = true;
        Game.openTiles++;
        this.text.setVisible(true);
        this.border.setFill(null);
        this.getChildren().remove(this.flagImage);
    }



}
