package com.example.qms.queue.exceptions;

public class QueueNotFoundException extends RuntimeException {
  public QueueNotFoundException(String message) {
    super(message);
  }

  public QueueNotFoundException() {
    super("Queue Not Found");
  }
}
