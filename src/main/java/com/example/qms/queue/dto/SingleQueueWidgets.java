package com.example.qms.queue.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SingleQueueWidgets {

    int queueLength;
    int queueLength_last_hour;

    long total_served_customers;
    long total_served_customers_last_hour;

    float avg_wait_time;
    float avg_serve_time;


}
