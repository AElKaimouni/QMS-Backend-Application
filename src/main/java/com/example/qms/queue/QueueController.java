package com.example.qms.queue;

import com.example.qms.queue.dto.CreateQueueDto;
import com.example.qms.queue.services.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
public class QueueController {

    @Autowired
    private QueueService queueService;

    // Endpoint to create a Queue using QueueDTO
    @PostMapping("/create")
    public ResponseEntity<Queue> createQueue(@Valid @RequestBody CreateQueueDto queueDTO) {
        Queue queue = queueService.createQueue(queueDTO);

        return ResponseEntity.ok(queue);
    }
}
