package com.example.qms.workspace.exceptions;

public class WorkspaceNotFoundException extends RuntimeException {
  public WorkspaceNotFoundException(String message) {
    super(message);
  }

  public WorkspaceNotFoundException() {
    super("Workspace is not found.");
  }
}
