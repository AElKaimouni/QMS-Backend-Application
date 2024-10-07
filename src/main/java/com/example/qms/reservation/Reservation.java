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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String token;
    private Integer position;
    private String email;
    @Enumerated(EnumType.STRING)
    private ReservationStatus status = ReservationStatus.PENDING;
    private Timestamp joinAt;



    public enum ReservationStatus{
        PENDING,
        CONFIRMED,    // Reservation has been confirmed
        CANCELED,     // Reservation has been canceled
        WAITING,      // Waiting for a spot or further processing
        COMPLETED,    // Reservation process has been completed
        EXPIRED,      // Reservation was not used or expired
        NO_SHOW,      // Reserved, but the user did not show up
    }
    @ManyToOne
    @JoinColumn(name = "queue_id", nullable = false)
    private Queue queue;
}
