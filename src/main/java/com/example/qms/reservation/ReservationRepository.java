package com.example.qms.reservation;

import com.example.qms.queue.Queue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    List<Reservation> findAllByJoinAtBetween(Timestamp timestamp, Timestamp timestamp1);

    List<Reservation> findAllByQueue(Optional<Queue> queue);
}
