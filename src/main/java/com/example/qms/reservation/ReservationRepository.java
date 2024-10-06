package com.example.qms.reservation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    List<Reservation> findAllByJoinAtBetween(Timestamp timestamp, Timestamp timestamp1);
}
