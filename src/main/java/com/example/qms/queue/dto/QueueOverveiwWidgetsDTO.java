package com.example.qms.queue.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueueOverveiwWidgetsDTO {
    long total_queues;
    long active_queues;

    long total_reservations;
    long yerserday_total_reservations;
    long last_hour_total_reservations;

    long total_served_customers;
    long yerserday_total_served_customers;
    long last_hour_total_served_customers;
}
