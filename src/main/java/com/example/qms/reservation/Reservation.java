package com.example.qms.reservation;

import com.example.qms.queue.Queue;
import com.example.qms.queue.config.QueueConfig;
import com.example.qms.queue.config.QueueConfigAttributeConverter;
import com.example.qms.reservation.enums.ReservationStatus;
import com.example.qms.reservation.info.ReservationInfoAttributeConverter;
import com.example.qms.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "queue_id", nullable = false)
    private UUID queueId;
    private Integer position;
    private String token;
    private String email;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status = ReservationStatus.WAITING;

    @Convert(converter = ReservationInfoAttributeConverter.class)
    @Column(name = "info", columnDefinition = "jsonb", length = 500)
    private JSONObject info;

    @Column(nullable = false)
    private Timestamp joinAt;
    private Timestamp calledAt;
    private Timestamp servedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "queue_id", insertable = false, updatable = false)
    private Queue queue;
}
