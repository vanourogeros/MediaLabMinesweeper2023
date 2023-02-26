package com.medialab.medialabminesweeper2023;

/**
 * An exception that is thrown when an invalid value is present
 * in the specified game description file upon starting the game
 * The message contains the error associated with the value.
 */
public class InvalidValueException extends Exception {

    String message;

    /**
     * Returns the error message associated with this exception.
     *
     * @return the error message
     */
    public String getErrorMessage() {
        return message;
    }

    /**
     * Constructs a new InvalidValueException with the specified detail message.
     *
     * @param s the detail message
     */
    public InvalidValueException(String s) {
        message = s;
    }
}
