package com.medialab.medialabminesweeper2023;

/**
 * Exception thrown when an invalid description file is encountered
 * when trying to start a game.
 */
public class InvalidDescriptionException extends Exception {

    String message;

    public String getErrorMessage() {
        return message;
    }
    public InvalidDescriptionException(String s) {
        message = s;
    }
}
