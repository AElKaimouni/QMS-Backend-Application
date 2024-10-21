package com.example.qms.reservation;

import com.example.qms.queue.Queue;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByQueueId(UUID queueId);
    Optional<Reservation> findByQueueIdAndPosition(UUID queueId, int position);
    Page<Reservation> findByQueueId(UUID queueId, Pageable pageable);
    Page<Reservation> findByQueueIdAndStatusIn(UUID queueId, List<String> states, Pageable pageable);
    Page<Reservation> findByQueueIdAndStatusNotIn(UUID queueId, List<String> states, Pageable pageable);
}
