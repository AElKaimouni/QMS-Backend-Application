package com.example.qms.analytics.queueSummary.dto;

import com.example.qms.queue.dto.QueueDailyPerformaceDTO;
import com.example.qms.queue.dto.QueueHourlyReservationsDTO;
import com.example.qms.queue.dto.QueueOverveiwWidgetsDTO;
import com.example.qms.reservation.dto.ReservationDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardDTO {
    QueueOverveiwWidgetsDTO widgets;
    List<QueueHourlyReservationsDTO> hourly_reservations;
    List<QueueDailyPerformaceDTO> queues_performance;
    List<ReservationDTO> recent_reservations;
}
