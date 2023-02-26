package com.medialab.medialabminesweeper2023;

import java.io.Serializable;

/**
 * Represents a game result, which includes the game outcome, the total time remaining, and the number of tries made.
 * This class is immutable and implements the Serializable interface to allow for object serialization.
 */
public record GameResult(String result, int totalTime, int tries) implements Serializable {

    /**
     * Returns the result of the game (e.g. "win" or "lose").
     *
     * @return the game result
     */
    public String getResult() {
        return result;
    }

    /**
     * Returns the total remaining time in seconds.
     *
     * @return the total remaining time.
     */
    public int getTotalTime() {
        return totalTime;
    }

    /**
     * Returns the number of tries made during the game.
     *
     * @return the number of tries
     */
    public int getTries() {
        return tries;
    }
}

