package com.example.qms.queue.services;

public interface QueueServiceInterface {
    public default String get(){
        return null;
    };

    public default int getNextPosition() {
        return 0;
    }

    public default String generateToken() {
        return null;
    }
}
