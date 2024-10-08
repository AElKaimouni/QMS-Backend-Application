package com.example.qms.queue;

import com.example.qms.queue.dto.CreateQueueDTO;
import com.example.qms.queue.dto.QueueConsultationInfoDTO;
import com.example.qms.queue.services.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.server.ResponseStatusException;

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
    public ResponseEntity<Queue> getQueue(@PathVariable UUID queueId) {
        Optional<Queue> queue = queueService.getQueue(queueId);

        if(queue.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        return ResponseEntity.ok(queue.get());
    }

    @GetMapping("/{queueId}/consult")
    public ResponseEntity<QueueConsultationInfoDTO> consultQueue(@PathVariable UUID queueId) {
        Optional<Queue> queue = queueService.getQueue(queueId);

        if(queue.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        QueueConsultationInfoDTO res = new QueueConsultationInfoDTO(queue.get());

        return ResponseEntity.ok(res);
    }

    @PostMapping
    public ResponseEntity<String> createQueue(
            @Valid @RequestBody CreateQueueDTO dto
    ) {
        String queueId = queueService.createQueue(dto);
        return ResponseEntity.ok(queueId);
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