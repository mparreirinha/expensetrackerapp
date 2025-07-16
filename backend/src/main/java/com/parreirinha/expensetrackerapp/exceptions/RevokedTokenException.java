package com.parreirinha.expensetrackerapp.exceptions;

public class RevokedTokenException extends RuntimeException {

    public RevokedTokenException(String message) {
        super(message);
    }

}
