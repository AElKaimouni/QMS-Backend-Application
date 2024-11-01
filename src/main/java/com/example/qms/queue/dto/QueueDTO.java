package com.example.qms.queue.dto;

import com.example.qms.queue.Queue;
import com.example.qms.queue.config.QueueConfig;
import com.example.qms.queue.enums.QueueStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class QueueDTO {
    private UUID id;
    private Long userId;
    private Long workspaceId;
    private String title;
    private String description;
    private int counter = 0; // current position of the queue
    private int length;
    private QueueStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private QueueConfig config;

    public QueueDTO(Queue queue) {
        this.setId(queue.getId());
        this.setUserId(queue.getUserId());
        this.setWorkspaceId(queue.getWorkspaceId());
        this.setTitle(queue.getTitle());
        this.setDescription(queue.getDescription());
        this.setLength(queue.getLength());
        this.setCounter(queue.getCounter());
        this.setStatus(queue.getStatus());
        this.setCreatedAt(queue.getCreatedAt());
        this.setUpdatedAt(queue.getUpdatedAt());
        this.setConfig(queue.getConfig());
    }
}
