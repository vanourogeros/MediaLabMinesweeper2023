package com.medialab.medialabminesweeper2023;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GameResultFileHandler {

    private static final int MAX_RECORDS = 5;
    private static final String FILE_NAME = "game_results.dat";

    // Create the file and initialize with an empty list of game results
    public static void createFile() throws IOException {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            List<GameResult> gameResults = new ArrayList<>();
            outputStream.writeObject(gameResults);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load the list of game results from the file
    public static List<GameResult> loadGameResults() {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            List<GameResult> gameResults = (List<GameResult>) inputStream.readObject();
            return gameResults;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Save a new game result to the file
    public static void saveGameResult(GameResult gameResult) {
        List<GameResult> gameResults = loadGameResults();
        gameResults.add(0, gameResult);
        List<GameResult> lastFiveResults = new ArrayList<>(gameResults.subList(0, Math.min(gameResults.size(), 5)));
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(lastFiveResults);
        } catch (IOException e) {
            System.err.println("Error saving game results to file: " + e.getMessage());
        }
    }
}

