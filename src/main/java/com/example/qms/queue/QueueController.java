package com.example.qms.queue;

import com.example.qms.queue.dto.CreateQueueRequest;
import com.example.qms.queue.services.QueueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.UUID;

@RestController
@RequestMapping("/queues")
public class QueueController {

    private final QueueService queueService;

    public QueueController(QueueService queueService) {
        this.queueService = queueService;
    }

    @GetMapping("/{queueId}")
    public ResponseEntity<Queue> getQueue(@PathVariable UUID queueId) {
        Queue queue = queueService.getQueue(queueId);
        return ResponseEntity.ok(queue);
    }

    @PostMapping
    public ResponseEntity<String> createQueue(
            @Valid @RequestBody CreateQueueRequest request
    ) {
        String queueId = queueService.createQueue(
                request.getTitle(),
                request.getLength(),
                Queue.QueueStatus.CREATED
        );
        return ResponseEntity.ok(queueId);
    }

    @PostMapping("/{queueId}/reserve")
    public ResponseEntity<Void> reserve(@PathVariable UUID queueId) {
        queueService.reserve(queueId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{queueId}/next")
    public ResponseEntity<Void> next(@PathVariable UUID queueId) {
        queueService.next(queueId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{queueId}")
    public ResponseEntity<Void> delete(@PathVariable UUID queueId) {
        queueService.delete(queueId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{queueId}/start")
    public ResponseEntity<Void> start(@PathVariable UUID queueId) {
        queueService.start(queueId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{queueId}/pause")
    public ResponseEntity<Void> pause(@PathVariable UUID queueId) {
        queueService.paused(queueId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{queueId}/close")
    public ResponseEntity<Void> close(@PathVariable UUID queueId) {
        queueService.close(queueId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/validate-token")
    public ResponseEntity<Boolean> validateToken() {
        boolean isValid = queueService.validateToken();
        return ResponseEntity.ok(isValid);
    }

    @PostMapping("/generate-token")
    public ResponseEntity<String> generateToken() {
        String token = queueService.generateToken();
        return ResponseEntity.ok(token);
    }
}