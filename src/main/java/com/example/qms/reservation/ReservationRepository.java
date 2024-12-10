package com.example.qms.reservation;

import com.example.qms.queue.Queue;
import com.example.qms.reservation.enums.ReservationStatus;
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

    // Count reservations by userId, status, and date range
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.queue.userId = :userId AND r.status = :status AND r.joinAt BETWEEN :startDate AND :endDate")
    long countReservationsByUserIdAndStatusAndDateRange(@Param("userId") Long userId,
                                                        @Param("status") ReservationStatus status,
                                                        @Param("startDate") Timestamp startDate,
                                                        @Param("endDate") Timestamp endDate);

    // Count reservations by userId and date range (without status)
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.queue.userId = :userId AND r.joinAt BETWEEN :startDate AND :endDate")
    long countReservationsByUserIdAndDateRange(@Param("userId") Long userId,
                                               @Param("startDate") Timestamp startDate,
                                               @Param("endDate") Timestamp endDate);

    // Query to fetch the last 'n' reservations for a specific user
    @Query("SELECT r FROM Reservation r WHERE r.queue.userId = :userId ORDER BY r.joinAt DESC LIMIT :limit")
    List<Reservation> findLastNReservationsByUserId(@Param("userId") Long userId, @Param("limit") int limit);

    List<Reservation> findAllByQueueId(UUID queueId);
    Optional<Reservation> findByQueueIdAndPosition(UUID queueId, int position);
    Page<Reservation> findByQueueId(UUID queueId, Pageable pageable);
    Page<Reservation> findByQueueIdAndStatusIn(UUID queueId, List<String> states, Pageable pageable);
    Page<Reservation> findByQueueIdAndStatusNotIn(UUID queueId, List<String> states, Pageable pageable);
}
