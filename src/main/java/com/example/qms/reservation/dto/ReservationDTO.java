package com.example.qms.reservation.dto;

import com.example.qms.queue.Queue;
import com.example.qms.reservation.Reservation;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDTO extends Reservation {

    @Enumerated(EnumType.STRING)
    private Reservation.ReservationStatus status = Reservation.ReservationStatus.WAITING;

    public ReservationDTO(Reservation reservation) {
        this.setId(reservation.getId());
        this.setPosition(reservation.getPosition());
        this.setStatus(reservation.getStatus());
        this.setQueueId(reservation.getQueueId());
        this.setEmail(reservation.getEmail());
        this.setToken(reservation.getToken());
        this.setJoinAt(reservation.getJoinAt());
        this.setServedAt(reservation.getServedAt());
    }
}
