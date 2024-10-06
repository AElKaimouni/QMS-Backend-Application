package com.example.qms.queue.services;

import com.example.qms.queue.Queue;

import java.util.UUID;

public interface QueueServiceInterface {
    public String createQueue(
            String title,
            int length,
            Queue.QueueStatus status
    );

    public void reserve(UUID queueId);

    public void next(UUID queueId);

    public void delete(UUID queueId);

    public void start(UUID queueId);

    public void paused(UUID queueId);

    public void close(UUID queueId);

    public boolean validateToken();

    public String generateToken();
}
