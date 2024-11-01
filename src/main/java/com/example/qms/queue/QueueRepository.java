package com.example.qms.queue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QueueRepository extends JpaRepository<Queue, UUID> {
    @Query(value =
        "SELECT AVG(EXTRACT(EPOCH FROM (r.served_at - r.called_at))) " +
        "FROM Reservation r " +
        "WHERE r.queue_id = :queueId " +
        "AND r.status = 'SERVED' " +
        "AND DATE(r.join_at) = CURRENT_DATE",
    nativeQuery = true)
    Optional<Double> findAverageServingTimeForQueue(@Param("queueId") UUID queueId);

    @Query("SELECT q.userId FROM Queue q WHERE q.id = :queueId")
    Long findUserIdByQueueId(@Param("queueId") UUID queueId);

    List<Queue> findByUserIdAndWorkspaceId(Long userId, Long workspaceId);
}