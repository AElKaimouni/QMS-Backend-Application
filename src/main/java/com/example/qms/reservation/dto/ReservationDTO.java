package com.example.qms.reservation.dto;

import com.example.qms.queue.Queue;
import com.example.qms.reservation.Reservation;
import com.example.qms.reservation.enums.ReservationStatus;
import com.example.qms.reservation.info.ReservationInfoAttributeConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDTO {
    private Long id;
    private UUID queueId;
    private Integer position;
    private String token;
    private String email;
    private ReservationStatus status;
    private Map<String, Object> info;
    private Timestamp joinAt;
    private Timestamp calledAt;
    private Timestamp servedAt;

    public ReservationDTO(Reservation reservation) {
        this.setId(reservation.getId());
        this.setPosition(reservation.getPosition());
        this.setStatus(reservation.getStatus());
        this.setQueueId(reservation.getQueueId());
        this.setEmail(reservation.getEmail());
        this.setToken(reservation.getToken());
        this.setJoinAt(reservation.getJoinAt());
        this.setCalledAt(reservation.getCalledAt());
        this.setServedAt(reservation.getServedAt());
        this.setInfo(reservation.getInfo().toMap());
    }
}
