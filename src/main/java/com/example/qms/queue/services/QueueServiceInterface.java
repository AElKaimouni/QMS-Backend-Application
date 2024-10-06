package com.example.qms.queue.services;

public interface QueueServiceInterface {
    public default int reserve() {
        return 0;
    }

    public default String generateToken() {
        return null;
    }
}
