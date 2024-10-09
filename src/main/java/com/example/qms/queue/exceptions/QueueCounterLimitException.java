package com.example.qms.queue.exceptions;

import com.example.qms.queue.Queue;

public class QueueCounterLimitException extends RuntimeException {
    public Queue queue;

    public QueueCounterLimitException(String message, Queue queue) {
        super(message);

        this.queue = queue;
    }

    public QueueCounterLimitException(Queue queue) {
        super("This Queue riches its limits");

        this.queue = queue;
    }
}
