package com.example.qms.user.exceptions;

public class EmailTakenExcepetion extends RuntimeException {
  public EmailTakenExcepetion(String message) {
    super(message);
  }
  public EmailTakenExcepetion() {
    super("Email is already taken");
  }

}
