package com.example.qms.reservation;

import com.example.qms.queue.Queue;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Reservation {
    @Id
    private UUID id = UUID.randomUUID();  // Manually generate UUID
    private String token;
    private Integer position;
    private String email;
    private String status;
    private Timestamp joinAt;

    @ManyToOne
    @JoinColumn(name = "queue_id", nullable = false)
    private Queue queue;
}
