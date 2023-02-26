package com.medialab.medialabminesweeper2023;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A utility class for writing game configuration files.
 */
public class GameWriter {

    /**
     * Writes a game configuration file with the specified parameters.
     *
     * @param scenarioID the scenario ID to use for the file name
     * @param numBombs the number of bombs in the game
     * @param superbomb the number of superbombs in the game
     * @param difficulty the difficulty level of the game
     * @param timeLimit the time limit for the game
     * @throws RuntimeException if an error occurs while writing the file
     */
    static void writeConfigFile(String scenarioID, int numBombs, int superbomb, int difficulty, int timeLimit) {
        String currentDirectory = System.getProperty("user.dir");
        String fileName = currentDirectory + "/multimedia/" + scenarioID + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(String.valueOf(difficulty));
            writer.newLine();
            writer.write(String.valueOf(numBombs));
            writer.newLine();
            writer.write(String.valueOf(timeLimit));
            writer.newLine();
            writer.write(String.valueOf(superbomb));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
