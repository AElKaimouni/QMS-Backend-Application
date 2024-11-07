package com.example.qms.queue.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class QueueOwnershipException extends RuntimeException{
    public QueueOwnershipException(String message) {
        super(message);
    }
}
