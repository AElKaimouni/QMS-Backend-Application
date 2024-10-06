package com.example.qms.queue.services;

import com.example.qms.queue.Queue;
import com.example.qms.queue.QueueRepository;
import com.example.qms.queue.dto.CreateQueueDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QueueService  implements QueueServiceInterface{

    @Autowired
    private QueueRepository queueRepository;

    public Queue createQueue(CreateQueueDto dto) {
        Queue queue = new Queue();
        queue.setTitle(dto.getTitle());
        queueRepository.save(queue);
        return queue;
    }

}
