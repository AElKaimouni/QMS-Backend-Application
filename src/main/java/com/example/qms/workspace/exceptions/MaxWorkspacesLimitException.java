package com.example.qms.workspace.exceptions;

public class MaxWorkspacesLimitException extends RuntimeException {
    public MaxWorkspacesLimitException(String message) {
        super(message);
    }
    public MaxWorkspacesLimitException() {
        super("you reached the max allowed workspaces");
    }
}
