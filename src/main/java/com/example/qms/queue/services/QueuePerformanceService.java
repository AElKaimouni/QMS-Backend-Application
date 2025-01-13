package com.example.qms.queue.services;

import com.example.qms.queue.QueueRepository;
import com.example.qms.queue.dto.QueueDailyPerformaceDTO;
import com.example.qms.queue.dto.QueueHourlyReservationsDTO;
import com.example.qms.queue.dto.QueueMetricsForADayDTO;
import com.example.qms.queue.dto.QueueOverveiwWidgetsDTO;
import com.example.qms.queue.enums.QueueStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class QueuePerformanceService {
    private final QueueRepository queueRepository;

    @Autowired
    private QueueService queueService;


    public QueuePerformanceService(QueueRepository queueRepository) {
        this.queueRepository = queueRepository;
    }

    public List<QueueHourlyReservationsDTO> getQueuesHourlyReservations(long user_id) {
        List<Object[]> results = queueRepository.getQueuesHourlyReservations(user_id);

        return results.stream().map(result -> new QueueHourlyReservationsDTO(
            (String) result[0],
            ((BigDecimal) result[1]).intValue(),
            (Long) result[2],
            (UUID) result[3]
        )).collect(Collectors.toList());
    }

    public List<QueueDailyPerformaceDTO> getQueuesDailyPerformance(long user_id) {
        List<Object[]> results = queueRepository.getQueuesDailyPerformance(user_id);

        return results.stream().map(result -> {
                    float avg_total_time=result[5] != null ? ((BigDecimal) result[5]).floatValue() : 0;
                    float avg_served_time=result[6] != null ? ((BigDecimal) result[6]).floatValue() : 0;
                    System.out.println(avg_served_time);
                    float avg_wait_time=avg_total_time-avg_served_time;

                    return new QueueDailyPerformaceDTO(
                            (UUID) result[0],
                            (String) result[1],
                            QueueStatus.valueOf((String) result[2]),
                            ((Long) result[3]).longValue(),
                            ((Long) result[4]).longValue(),
                            avg_total_time,
                            avg_served_time,
                            avg_wait_time

                    );
                }
        ).collect(Collectors.toList());
    }

    public List<QueueMetricsForADayDTO> getQueueMonthlyPerformance(long user_id, UUID queueId) {
        // Fetch monthly performance data from the repository
        List<Object[]> results = queueRepository.getQueueMonthlyPerformance(queueId,user_id);
        System.out.println(results);
        // Transform the raw results into DTOs
        return results.stream().map(result -> {
            // Handle avg_total_time and avg_served_time safely
            float avg_total_time = result[4] != null
                    ? (result[4] instanceof BigDecimal
                    ? ((BigDecimal) result[4]).floatValue()
                    : BigDecimal.valueOf((Long) result[4]).floatValue())
                    : 0;

            float avg_served_time = result[5] != null
                    ? (result[5] instanceof BigDecimal
                    ? ((BigDecimal) result[5]).floatValue()
                    : BigDecimal.valueOf((Long) result[5]).floatValue())
                    : 0;
            float avg_wait_time = avg_total_time - avg_served_time;

            return new QueueMetricsForADayDTO(
                    (Date) result[0],//day
                    ((Long) result[2]).longValue(),//total reservations
                    ((Long) result[3]).longValue(),//total served reservations
                    avg_total_time,
                    avg_served_time,
                    avg_wait_time
            );
        }).collect(Collectors.toList());
    }
    public List<QueueMetricsForADayDTO> getQueueWeeklyPerformance(long userId, UUID queueId) {
        // Fetch weekly performance data from the repository
        List<Object[]> results = queueRepository.getQueueWeeklyPerformance(queueId,userId);

        // Transform the raw results into DTOs
        return results.stream().map(result -> {
            // Handle avg_total_time and avg_served_time safely
            float avg_total_time = result[4] != null
                    ? (result[4] instanceof BigDecimal
                    ? ((BigDecimal) result[4]).floatValue()
                    : BigDecimal.valueOf((Long) result[4]).floatValue())
                    : 0;

            float avg_served_time = result[5] != null
                    ? (result[5] instanceof BigDecimal
                    ? ((BigDecimal) result[5]).floatValue()
                    : BigDecimal.valueOf((Long) result[5]).floatValue())
                    : 0;
            float avg_wait_time = avg_total_time - avg_served_time;

            return new QueueMetricsForADayDTO(
                    (Date) result[0],//day
                    ((Long) result[2]).longValue(),//total reservations
                    ((Long) result[3]).longValue(),//total served reservations
                    avg_total_time,
                    avg_served_time,
                    avg_wait_time                   // Average Wait Time
            );
        }).collect(Collectors.toList());
    }




}
