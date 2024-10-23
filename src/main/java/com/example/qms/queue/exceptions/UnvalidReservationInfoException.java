package com.example.qms.queue.exceptions;

public class UnvalidReservationInfoException extends RuntimeException {
    public UnvalidReservationInfoException(String message) {
        super(message);
    }
    public UnvalidReservationInfoException() {

        super("unvalid reservation informations.");
    }
}
