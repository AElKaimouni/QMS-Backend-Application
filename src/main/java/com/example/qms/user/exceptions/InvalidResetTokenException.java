package com.example.qms.user.exceptions;

public class InvalidResetTokenException extends Exception {
    public InvalidResetTokenException(String message) {
        super(message);
    }
}
