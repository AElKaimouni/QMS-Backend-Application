package com.example.qms.queue;

import com.example.qms.queue.config.QueueConfig;
import com.example.qms.queue.config.QueueConfigAttributeConverter;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@NoArgsConstructor
public class Queue {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @JsonIgnore
    private UUID secret;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int counter = 0; // current position of the queue

    @Column(nullable = false)
    private int length;

    public enum QueueStatus {
        CREATED,
        ACTIVE,
        PAUSED,
        CLOSED,
        DELETED
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QueueStatus status = QueueStatus.CREATED;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Convert(converter = QueueConfigAttributeConverter.class)
    @Column(name = "config", columnDefinition = "jsonb", length = 500)
    private QueueConfig config;


    public Queue(String title, String description, QueueConfig config, Long userId) {
        this.title = title;
        this.userId=userId;
        this.description = description;
        this.length = 0;
        this.counter = 0;

        this.setConfig(config);
    }
}
