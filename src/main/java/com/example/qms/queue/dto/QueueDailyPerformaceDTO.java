package com.example.qms.queue.dto;

import com.example.qms.queue.enums.QueueStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueueDailyPerformaceDTO {
    UUID id;
    String title;
    QueueStatus status;
    long total_reservations;
    long served_reservations;
    float avg_total_time;
}
