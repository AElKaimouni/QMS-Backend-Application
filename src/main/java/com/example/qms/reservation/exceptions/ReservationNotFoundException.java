package com.example.qms.reservation.exceptions;

public class ReservationNotFoundException extends RuntimeException {
    public ReservationNotFoundException(String message) {
        super(message);
    }

    public ReservationNotFoundException() {
        super("Reservation Not Found");
    }
}
