package com.example.qms.queue;

import com.example.qms.queue.dto.QueueConsultationInfoDTO;
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
    private String title;

    @Column(nullable = false)
    private String description;

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
    private QueueStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    public Queue(String title, String description) {
        this.title = title;
        this.description = description;
        this.length = 0;
        this.status = QueueStatus.CREATED;
    }
}
