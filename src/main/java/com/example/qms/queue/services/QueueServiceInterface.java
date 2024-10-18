package com.example.qms.queue.services;

import com.example.qms.queue.Queue;
import com.example.qms.queue.dto.CreateQueueDTO;

import java.util.UUID;

public interface QueueServiceInterface {
    public String createQueue(CreateQueueDTO dto,Long userId);

    public Integer reserve(UUID queueId);

    public Queue next(UUID queueId);

    public void delete(UUID queueId);

    public void start(UUID queueId);

    public void paused(UUID queueId);

    public void close(UUID queueId);

    public Integer validateToken(String encryptedTicket, UUID qid);

    public String generateToken(int position, UUID qid);
}
