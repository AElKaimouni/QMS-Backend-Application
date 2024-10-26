package com.example.qms.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class TokenNotFoundException extends ResponseStatusException {

     public TokenNotFoundException(String message) {
            super(HttpStatus.NOT_FOUND, message);
        }


}
