package com.example.qms.reservation;

import com.example.qms.queue.Queue;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID queueId;
    private String token;
    private Integer position;
    private String email;
    @Enumerated(EnumType.STRING)
    private ReservationStatus status = ReservationStatus.WAITING;
    @Column(nullable = false)
    private Timestamp joinAt;
    private Timestamp servedAt;

    public enum ReservationStatus{
        WAITING,
        SERVING,
        CANCELED,     // Reservation has been canceled
        SERVED,    // Reservation process has been completed
        EXPIRED,      // Reservation was not used or expired
    }
}
