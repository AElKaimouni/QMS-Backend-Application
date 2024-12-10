package com.example.qms.queue.services;

import com.example.qms.queue.QueueRepository;
import com.example.qms.queue.dto.QueueDailyPerformaceDTO;
import com.example.qms.queue.dto.QueueHourlyReservationsDTO;
import com.example.qms.queue.dto.QueueOverveiwWidgetsDTO;
import com.example.qms.queue.enums.QueueStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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


        return results.stream().map(result -> new QueueDailyPerformaceDTO(
                (UUID) result[0],
                (String) result[1],
                QueueStatus.valueOf((String) result[2]),
                ((Long) result[3]).longValue(),
                ((Long) result[4]).longValue(),
                result[5] != null ? ((BigDecimal) result[5]).floatValue() : 0
        )).collect(Collectors.toList());
    }
}
