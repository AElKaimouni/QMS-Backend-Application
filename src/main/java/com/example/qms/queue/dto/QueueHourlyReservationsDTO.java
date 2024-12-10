package com.example.qms.queue.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueueHourlyReservationsDTO {
    String title;
    int hour;
    long reservations_count;
    UUID queue_id;
}