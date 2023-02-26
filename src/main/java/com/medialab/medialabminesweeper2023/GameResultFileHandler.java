package com.medialab.medialabminesweeper2023;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The GameResultFileHandler class is responsible for managing the storage and retrieval of game results.
 */
public class GameResultFileHandler {

    private static final int MAX_RECORDS = 5;
    private static final String FILE_NAME = "./multimedia/game_results.dat";

    /**
     * Creates the file to store the game results and initializes it with an empty list of game results.
     *
     * @throws IOException if an I/O error occurs while creating the file.
     */
    public static void createFile() throws IOException {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            List<GameResult> gameResults = new ArrayList<>();
            outputStream.writeObject(gameResults);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the list of game results from the file.
     *
     * @return the list of game results, or an empty list if the file does not exist or cannot be read.
     */
    public static List<GameResult> loadGameResults() {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            List<GameResult> gameResults = (List<GameResult>) inputStream.readObject();
            return gameResults;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Saves a new game result to the file.
     *
     * @param gameResult the game result to save.
     */
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

