package com.medialab.medialabminesweeper2023;

public class InvalidValueException extends Exception {

    String message;

    public String getErrorMessage() {
        return message;
    }

    public InvalidValueException(String s) {
        message = s;
    }
}
