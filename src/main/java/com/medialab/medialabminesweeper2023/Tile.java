package com.medialab.medialabminesweeper2023;

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

import static com.medialab.medialabminesweeper2023.Game.getNeighbors;

public class Tile extends StackPane {
    static int TILE_SIZE = 40;
    int x, y;
    boolean hasBomb;
    boolean isOpen = false;
    boolean isMarked = false;
    ImageView flagImage;
    Rectangle border = new Rectangle(TILE_SIZE - 2, TILE_SIZE - 2);
    Text text = new Text();

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
            } else if (e.getButton() == MouseButton.SECONDARY) {
                if (!this.isOpen) // Can only mark closed tiles
                    Game.mark(this);
            }
        });
    }





}
