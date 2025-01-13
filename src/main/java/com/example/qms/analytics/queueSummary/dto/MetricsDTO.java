package com.example.qms.analytics.queueSummary.dto;
import com.example.qms.queue.dto.QueueDailyPerformaceDTO;
import com.example.qms.queue.dto.QueueDetailsDTO;
import com.example.qms.queue.dto.QueueMetricsForADayDTO;
import com.example.qms.queue.dto.SingleQueueWidgets;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetricsDTO {
    QueueDetailsDTO queueDetails;
    SingleQueueWidgets widgets;
    List<QueueMetricsForADayDTO> queueMonthlyPerformance;
    List<QueueMetricsForADayDTO> queueWeeklyPerformance;

}
