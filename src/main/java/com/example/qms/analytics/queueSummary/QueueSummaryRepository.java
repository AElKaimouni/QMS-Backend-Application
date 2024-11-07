package com.example.qms.analytics.queueSummary;

import com.example.qms.analytics.queueSummary.dto.QueueMetricsDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.UUID;

public interface QueueSummaryRepository extends JpaRepository<QueueSummary, Long> {


    //Metrics Per Queue
    @Query("SELECT new com.example.qms.analytics.queueSummary.dto.QueueMetricsDTO(SUM(q.visitorsCount), AVG(q.averageWaitTime), AVG(q.averageServeTime)) " +
            "FROM QueueSummary q " +
            "WHERE q.queue.id = :queueId AND q.date BETWEEN :startDate AND :endDate")
    QueueMetricsDTO getQueueMetrics(@Param("queueId") UUID queueId,
                                    @Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);



    //Metrics Per Workspace
    @Query("SELECT new com.example.qms.analytics.queueSummary.dto.QueueMetricsDTO(SUM(q.visitorsCount), AVG(q.averageWaitTime), AVG(q.averageServeTime)) " +
            "FROM QueueSummary q " +
            "WHERE q.workspace.id = :workspaceId AND q.date BETWEEN :startDate AND :endDate")
    QueueMetricsDTO getWorkspaceMetrics(@Param("workspaceId") Long wokspaceId,
                                    @Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);








}
