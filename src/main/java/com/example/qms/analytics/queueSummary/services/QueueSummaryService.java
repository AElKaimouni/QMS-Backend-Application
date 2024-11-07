package com.example.qms.analytics.queueSummary.services;
import com.example.qms.analytics.queueSummary.QueueSummaryRepository;
import com.example.qms.analytics.queueSummary.dto.QueueMetricsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class QueueSummaryService {


    @Autowired
    private QueueSummaryRepository queueSummaryRepository;


    public QueueMetricsDTO getQueueMetrics(UUID queueId, LocalDate startDate, LocalDate endDate) {
        return queueSummaryRepository.getQueueMetrics(queueId, startDate, endDate);
    }

    public QueueMetricsDTO getWorkspaceMetrics(Long workspaceId, LocalDate startDate, LocalDate endDate) {
        return queueSummaryRepository.getWorkspaceMetrics(workspaceId, startDate, endDate);

    }


}
