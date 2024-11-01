package com.example.qms.workspace.exceptions;

public class MinWorkspacesLimitException extends RuntimeException {
    public MinWorkspacesLimitException(String message) {
        super(message);
    }
    public MinWorkspacesLimitException() {
        super("you cannot remove the only workspace");
    }
}
