package com.example.qms.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserAlreadyVerifiedException extends ResponseStatusException {
    public UserAlreadyVerifiedException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
