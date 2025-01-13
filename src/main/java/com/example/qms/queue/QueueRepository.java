package com.example.qms.queue;

import com.example.qms.queue.dto.QueueHourlyReservationsDTO;
import com.example.qms.queue.dto.QueueDailyPerformaceDTO;
import com.example.qms.queue.enums.QueueStatus;
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

    @Query(value = "SELECT \n" +
            "\tq.title,\n" +
            "    EXTRACT(HOUR FROM join_at) AS hour,\n" +
            "    COUNT(*) AS reservations_count, queue_id\n" +
            "FROM \n" +
            "    reservation\n" +
            "LEFT JOIN queue q ON q.id = queue_id\n" +
            "WHERE \n" +
            "\tq.user_id = :uid AND\n" +
            "    join_at::DATE = CURRENT_DATE -- Specify the date\n" +
            "\tAND EXTRACT(HOUR FROM join_at) <= 24 AND EXTRACT(HOUR FROM join_at) >= 0\n" +
            "GROUP BY \n" +
            "    hour, queue_id, q.title\n" +
            "ORDER BY \n" +
            "    queue_id, hour;", nativeQuery = true)
    List<Object[]> getQueuesHourlyReservations(@Param("uid") long user_id);

    @Query(value = "SELECT \n" +
            "\tq.id, q.title, q.status, q.total_reservations, \n" +
            "\tCOUNT(r2.*) AS served_reservations ,\n" +
            "\tAVG(EXTRACT(EPOCH FROM (r2.served_at - r2.join_at))) as avg_total_time,\n" +
            "\tAVG(EXTRACT(EPOCH FROM (r2.served_at - r2.called_at))) as avg_served_time\n" +
            "FROM (\n" +
            "\tSELECT \n" +
            "\t\tq.id, q.title, q.status,\n" +
            "\t\tCOUNT(r1.*) AS total_reservations\n" +
            "\tFROM queue q\n" +
            "\tLEFT JOIN reservation r1 ON \n" +
            "\t\tr1.queue_id = q.id\n" +
            "\t\tAND r1.join_at::DATE = CURRENT_DATE\n" +
            "\tWHERE q.user_id = :uid\n" +
            "\tGROUP BY q.id, q.title\n" +
            ") as q\n" +
            "LEFT JOIN reservation r2 ON \n" +
            "\tr2.queue_id = q.id\n" +
            "\tAND r2.status = 'SERVED'\n" +
            "\tAND r2.join_at::DATE = CURRENT_DATE \n" +
            "GROUP BY q.id, q.title, q.status, q.total_reservations\n" +
            "ORDER BY total_reservations DESC LIMIT 5", nativeQuery = true)
    List<Object[]> getQueuesDailyPerformance(@Param("uid") long user_id);

    @Query(value = "SELECT DATE(r2.join_at) AS day, " +
            "q.id, COUNT(r1.*) AS total_reservations, " +
            "COUNT(r2.*) AS served_reservations, " +
            "AVG(EXTRACT(EPOCH FROM (r2.served_at - r2.join_at))) AS avg_total_time, " +
            "AVG(EXTRACT(EPOCH FROM (r2.served_at - r2.called_at))) AS avg_served_time " +
            "FROM queue q " +
            "LEFT JOIN reservation r1 ON r1.queue_id = q.id " +
            "AND r1.join_at >= CURRENT_DATE - INTERVAL '1 MONTH' " +
            "LEFT JOIN reservation r2 ON r2.queue_id = q.id " +
            "AND r2.status = 'SERVED' " +
            "AND r2.join_at >= CURRENT_DATE - INTERVAL '1 MONTH' " +
            "WHERE q.user_id = :userId AND q.id = :queueId " +
            "GROUP BY DATE(r2.join_at), q.id " +
            "ORDER BY day ASC", nativeQuery = true)
    List<Object[]> getQueueMonthlyPerformance(@Param("queueId") UUID queueId, @Param("userId") long userId);

    @Query(value = "SELECT DATE(r2.join_at) AS day, " +
            "q.id, COUNT(r1.*) AS total_reservations, " +
            "COUNT(r2.*) AS served_reservations, " +
            "AVG(EXTRACT(EPOCH FROM (r2.served_at - r2.join_at))) AS avg_total_time, " +
            "AVG(EXTRACT(EPOCH FROM (r2.served_at - r2.called_at))) AS avg_served_time " +
            "FROM queue q " +
            "LEFT JOIN reservation r1 ON r1.queue_id = q.id " +
            "AND r1.join_at >= CURRENT_DATE - INTERVAL '7 days' " +
            "LEFT JOIN reservation r2 ON r2.queue_id = q.id " +
            "AND r2.status = 'SERVED' " +
            "AND r2.join_at >= CURRENT_DATE - INTERVAL '7 days' " +
            "WHERE q.user_id = :userId AND q.id = :queueId " +
            "GROUP BY DATE(r2.join_at), q.id " +
            "ORDER BY day ASC", nativeQuery = true)
    List<Object[]> getQueueWeeklyPerformance(@Param("queueId") UUID queueId, @Param("userId") long userId);


    @Query("SELECT q.userId FROM Queue q WHERE q.id = :queueId")
    Long findUserIdByQueueId(@Param("queueId") UUID queueId);

    List<Queue> findByUserIdAndWorkspaceId(Long userId, Long workspaceId);

    @Query("SELECT COUNT(q) FROM Queue q WHERE q.userId = :userId")
    long countTotalQueuesByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(q) FROM Queue q WHERE q.userId = :userId AND q.status = :status")
    long countQueuesByUserIdAndStatus(@Param("userId") Long userId, @Param("status") QueueStatus status);

    @Query(value = """
    SELECT COUNT(*) 
    FROM reservation r
    WHERE r.queue_id = :queueId
    AND r.join_at >= NOW() - INTERVAL '1 hour'
    """, nativeQuery = true)
    int getLastHourQueueLength(@Param("queueId") UUID queueId);

}