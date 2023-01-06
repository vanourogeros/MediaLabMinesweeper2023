package com.medialab.medialabminesweeper2023;

import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import static com.medialab.medialabminesweeper2023.Game.getNeighbors;

public class Tile extends StackPane {
    static int TILE_SIZE = 40;
    int x, y;
    boolean hasBomb;
    boolean isOpen = false;
    private boolean isMarked = false;
    private int bombs = 0;
    Rectangle border = new Rectangle(TILE_SIZE - 2, TILE_SIZE - 2);
    Text text = new Text();

    public Tile(int x, int y, boolean hasBomb){
        this.x = x;
        this.y = y;
        this.hasBomb = hasBomb;

        border.setStroke(Color.LIGHTGRAY);
        border.setFill(Color.SLATEGRAY);

        text.setFont(Font.font(18));
        text.setText(hasBomb ? "X" : "");
        text.setVisible(false);

        getChildren().addAll(border, text);

        setTranslateX(x * TILE_SIZE);
        setTranslateY(y * TILE_SIZE);

        setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                Game.open(this);
            } else if (e.getButton() == MouseButton.SECONDARY) {
                mark();
            }
        });
    }



    public void mark() {
        if (isMarked) {
            isMarked = false;
            border.setFill(Color.SLATEGRAY);
            return;
        }

        isMarked = true;
        border.setFill(Color.RED);
    }

}
