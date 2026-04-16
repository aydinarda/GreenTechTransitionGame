package com.greentech.game.exception;

public class IllegalGameStateException extends RuntimeException {
    public IllegalGameStateException(String message) {
        super(message);
    }
}
