package com.example.qms.analytics.queueSummary;

import com.example.qms.analytics.queueSummary.dto.DateRangeDTO;
import com.example.qms.analytics.queueSummary.dto.QueueMetricsDTO;
import com.example.qms.analytics.queueSummary.services.QueueSummaryService;
import com.example.qms.queue.services.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/queue-summary")
public class QueueSummaryController {
    @Autowired
    private  QueueService queueService;
    @Autowired
    private  QueueSummaryService queueSummaryService;

    // Endpoint to get aggregated metrics for a specific queue
    @PostMapping("/{queueId}/metrics")
    public ResponseEntity<QueueMetricsDTO> getQueueMetrics(@PathVariable UUID queueId,
                                                           @RequestBody DateRangeDTO dateRange) {
        queueService.verifyOwnership(queueId);
        QueueMetricsDTO metrics = queueSummaryService.getQueueMetrics(queueId, dateRange.getStartDate(), dateRange.getEndDate());
        return ResponseEntity.ok(metrics);
    }

    // Endpoint to get aggregated metrics for a specific workspace
    @PostMapping("/workspace/{workspaceId}/metrics")
    public ResponseEntity<QueueMetricsDTO> getWorkspaceMetrics(@PathVariable Long workspaceId,
                                                               @RequestBody DateRangeDTO dateRange) {
        QueueMetricsDTO workspaceMetrics = queueSummaryService.getWorkspaceMetrics(workspaceId, dateRange.getStartDate(), dateRange.getEndDate());
        return ResponseEntity.ok(workspaceMetrics);
    }

}
