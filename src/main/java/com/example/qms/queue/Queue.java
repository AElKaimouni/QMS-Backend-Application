package com.example.qms.queue;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
public class Queue {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID secret;

    @Column(nullable = false)
    private String title;

    private int counter = 0;

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
    private QueueStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    public Queue(String title, int length, QueueStatus status) {
        this.title = title;
        this.length = length;
        this.status = status;
    }

}
