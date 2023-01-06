package com.medialab.medialabminesweeper2023;

public class InvalidDescriptionException extends Exception {

    String message;

    public String getErrorMessage() {
        return message;
    }
    public InvalidDescriptionException(String s) {
        message = s;
    }
}
