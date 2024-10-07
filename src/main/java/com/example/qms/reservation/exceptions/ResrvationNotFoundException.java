package com.example.qms.reservation.exceptions;

public class ResrvationNotFoundException extends RuntimeException {
    public ResrvationNotFoundException(String message) {
        super(message);
    }

    public ResrvationNotFoundException() {
        super("Reservation Not Found");
    }
}
