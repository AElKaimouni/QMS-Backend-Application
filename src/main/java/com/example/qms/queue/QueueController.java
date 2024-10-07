package com.example.qms.queue;

import com.example.qms.queue.dto.CreateQueueDto;
import com.example.qms.queue.services.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/queue")
public class QueueController {

    @Autowired
    private QueueService queueService;

    @GetMapping("/{qid}/validate/{token}")
    public Integer validateToken(
            @PathVariable("qid") String qid,
            @PathVariable("token") String token
    ) {
        // get Queue Secret
        String queueSecret = queueService.getQueueSecret(qid);

        // string to uuid
        UUID queueId = UUID.fromString(qid);

        return queueService.validateToken(token, queueId);
    }

    @GetMapping("/{queueId}")
    public ResponseEntity<Optional<Queue>> getQueue(@PathVariable UUID queueId) {
        Optional<Queue> queue = queueService.getQueue(queueId);

        return ResponseEntity.ok(queue);
    }

    @PostMapping
    public ResponseEntity<String> createQueue(
            @Valid @RequestBody CreateQueueDto request
    ) {
        String queueId = queueService.createQueue(
                request.getTitle(),
                request.getLength(),
                Queue.QueueStatus.CREATED
        );
        return ResponseEntity.ok(queueId);
    }

    @PostMapping("/{queueId}/reserve")
    public ResponseEntity<Map<String, String>> reserve(@PathVariable UUID queueId) {
        var length = queueService.reserve(queueId);
        return ResponseEntity.ok(Map.of("length", Integer.toString(length)));
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

}