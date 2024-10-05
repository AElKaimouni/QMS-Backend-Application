package com.example.qms.queue.services;

import com.example.qms.queue.Queue;
import com.example.qms.queue.dto.CreateQueueDto;
import org.springframework.stereotype.Service;

@Service
public class QueueService {
    public Queue createQueue(CreateQueueDto dto) {
        return new Queue();
    }
}
