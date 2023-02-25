package com.medialab.medialabminesweeper2023;

import java.io.Serializable;

public record GameResult(String result, int totalTime, int tries) implements Serializable {

    public String getResult() {
        return result;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public int getTries() {
        return tries;
    }
}

