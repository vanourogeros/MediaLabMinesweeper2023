package com.medialab.medialabminesweeper2023;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

/**
 * This class implements the event handler for showing the game records in a table.
 * It loads the game results from a file, creates a table with the top 5 results,
 * and displays the table in a new window.
 */

public class ShowRecordsHandler implements EventHandler<ActionEvent> {

    /**
     * Handles the "Records" button click event. Loads the game results from the file,
     * displays them in a table, and shows a new window with the table.
     *
     * @param event The ActionEvent object representing the button click event.
     */
    @Override
    public void handle(ActionEvent event) {
        // Load the game results from the file
        List<GameResult> gameResults = GameResultFileHandler.loadGameResults();

        // Set up the table columns
        TableColumn<GameResult, String> resultColumn = new TableColumn<>("Result");
        TableColumn<GameResult, Integer> timeColumn = new TableColumn<>("Time (s)");
        TableColumn<GameResult, Integer> triesColumn = new TableColumn<>("Tries");

        // Set the cell value factories for each column
        resultColumn.setCellValueFactory(new PropertyValueFactory<>("result"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("totalTime"));
        triesColumn.setCellValueFactory(new PropertyValueFactory<>("tries"));

        // Set up the table
        TableView<GameResult> table = new TableView<>();
        table.setItems(FXCollections.observableList(gameResults.subList(0, Math.min(gameResults.size(), 5))));
        table.getColumns().addAll(resultColumn, timeColumn, triesColumn);
        table.setPrefSize(300, 150);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Format the table cells
        resultColumn.setCellFactory(column -> {
            return new TableCell<GameResult, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? "" : item);
                    setGraphic(null);
                    if (!empty) {
                        if (item.equals("WIN")) {
                            setTextFill(Color.GREEN);
                            setStyle("-fx-font-weight: bold;");
                        } else if (item.equals("LOSE")) {
                            setTextFill(Color.RED);
                            setStyle("-fx-font-weight: bold;");
                        }
                        setAlignment(Pos.CENTER);
                    }
                }
            };
        });
        timeColumn.setCellFactory(column -> {
            return new TableCell<GameResult, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? "" : item.toString());
                    setGraphic(null);
                    if (!empty) {
                        setAlignment(Pos.CENTER);
                    }
                }
            };
        });
        triesColumn.setCellFactory(column -> {
            return new TableCell<GameResult, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? "" : item.toString());
                    setGraphic(null);
                    if (!empty) {
                        setAlignment(Pos.CENTER);
                    }
                }
            };
        });

        // Set up the stage
        Stage stage = new Stage();
        stage.setTitle("Game Records");
        Scene scene = new Scene(table);
        stage.setScene(scene);
        stage.show();
    }
}

