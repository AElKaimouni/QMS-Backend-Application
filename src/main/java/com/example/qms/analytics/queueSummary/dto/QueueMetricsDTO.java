package com.example.qms.analytics.queueSummary.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueueMetricsDTO {
    private Long totalVisitors;
    private Double averageWaitTime;
    private Double averageServeTime;

}
