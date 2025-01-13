package com.example.qms.queue.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueueMetricsForADayDTO {
    Date day;
    long total_reservations;
    long served_reservations;
    float avg_total_time;
    float avg_served_time;
    float avg_wait_time;
}
