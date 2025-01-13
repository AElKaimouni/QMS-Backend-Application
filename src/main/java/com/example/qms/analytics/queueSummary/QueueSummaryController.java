package com.example.qms.analytics.queueSummary;

import com.example.qms.analytics.queueSummary.dto.DashboardDTO;
import com.example.qms.analytics.queueSummary.dto.DateRangeDTO;
import com.example.qms.analytics.queueSummary.dto.MetricsDTO;
import com.example.qms.analytics.queueSummary.dto.QueueMetricsDTO;
import com.example.qms.analytics.queueSummary.services.QueueSummaryService;
import com.example.qms.queue.dto.*;
import com.example.qms.queue.enums.QueueStatus;
import com.example.qms.queue.services.QueuePerformanceService;
import com.example.qms.queue.services.QueueService;
import com.example.qms.reservation.dto.ReservationDTO;
import com.example.qms.reservation.enums.ReservationStatus;
import com.example.qms.reservation.services.ReservationService;
import com.example.qms.user.config.CustomUserDetails;
import com.example.qms.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/queue-summary")
public class QueueSummaryController {
    @Autowired
    private  QueueService queueService;
    @Autowired
    private  QueueSummaryService queueSummaryService;
    @Autowired
    private UserService userService;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private QueuePerformanceService queuePerformanceService;

    // Endpoint to get aggregated metrics for a specific queue
    /**
    @PostMapping("/{queueId}/metrics")
    public ResponseEntity<QueueMetricsDTO> getQueueMetrics(@PathVariable UUID queueId,
                                                           @RequestBody DateRangeDTO dateRange) {
        queueService.verifyOwnership(queueId);
        QueueMetricsDTO metrics = queueSummaryService.getQueueMetrics(queueId, dateRange.getStartDate(), dateRange.getEndDate());
        return ResponseEntity.ok(metrics);
    }
   **/

    // Endpoint to get aggregated metrics for a specific workspace
    @PostMapping("/workspace/{workspaceId}/metrics")
    public ResponseEntity<QueueMetricsDTO> getWorkspaceMetrics(@PathVariable Long workspaceId,
                                                               @RequestBody DateRangeDTO dateRange) {
        QueueMetricsDTO workspaceMetrics = queueSummaryService.getWorkspaceMetrics(workspaceId, dateRange.getStartDate(), dateRange.getEndDate());
        return ResponseEntity.ok(workspaceMetrics);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> todayPerformance() {
        CustomUserDetails userDetails = userService.auth();
        long user_id = userDetails.getId();
        List<QueueDailyPerformaceDTO> daily_performance = queuePerformanceService.getQueuesDailyPerformance(user_id);
        List<QueueHourlyReservationsDTO> hourly_performance = queuePerformanceService.getQueuesHourlyReservations(user_id);

        QueueOverveiwWidgetsDTO queues_widgets = new QueueOverveiwWidgetsDTO();
        long total_queues = queueService.getQueuesCount(user_id);
        long total_active_queues = queueService.getQueuesCount(user_id, QueueStatus.ACTIVE);

        long total_reservations = reservationService.getTodayReservationsCount(user_id);
        long yesterday_reservations = reservationService.getYesterdayReservationsCount(user_id);
        long last_hour_reservations = reservationService.getLastHourReservationsCount(user_id);

        long total_served_customers = reservationService.getTodayReservationsCount(user_id, ReservationStatus.SERVED);
        long yesterday_served_customers = reservationService.getYesterdayReservationsCount(user_id, ReservationStatus.SERVED);
        long last_hour_served_customers = reservationService.getLastHourReservationsCount(user_id, ReservationStatus.SERVED);

        queues_widgets.setActive_queues(total_active_queues);
        queues_widgets.setTotal_queues(total_queues);
        queues_widgets.setLast_hour_total_reservations(last_hour_reservations);
        queues_widgets.setTotal_reservations(total_reservations);
        queues_widgets.setTotal_served_customers(total_served_customers);
        queues_widgets.setLast_hour_total_served_customers(last_hour_served_customers);
        queues_widgets.setYerserday_total_reservations(yesterday_reservations);
        queues_widgets.setYerserday_total_served_customers(yesterday_served_customers);

        List<ReservationDTO> recent_reservations = reservationService.getRecentReservationsForUser(user_id, 5);


        DashboardDTO dto = new DashboardDTO();


        dto.setQueues_performance(daily_performance);
        dto.setHourly_reservations(hourly_performance);
        dto.setWidgets(queues_widgets);
        dto.setRecent_reservations(recent_reservations);

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/metrics/{queueId}")
    public ResponseEntity<MetricsDTO> todayQueueMetrics(@PathVariable UUID queueId) {
        CustomUserDetails userDetails = userService.auth();
        long user_id = userDetails.getId();
        List<QueueMetricsForADayDTO> monthly_performance = queuePerformanceService.getQueueMonthlyPerformance(user_id,queueId);
        List<QueueMetricsForADayDTO> weekly_performance = queuePerformanceService.getQueueWeeklyPerformance(user_id,queueId);



        SingleQueueWidgets widgets = new SingleQueueWidgets();
        int queue_length = queueService.getQueueLength(queueId);
        int queue_length_last_hour=queueService.getQueueLengthLastHour(queueId);

        long total_served_customers=reservationService.getTodayReservationsServedByQueue(user_id, queueId);
        long served_customers_last_hour=reservationService.getLastHourReservationsServedByQueue(user_id, queueId);

        QueueMetricsForADayDTO lastDayPerformance = weekly_performance.get(weekly_performance.size() - 1);
        // Extract avgWaitTime and avgServeTime
        float avgWaitTime = lastDayPerformance.getAvg_wait_time();
        float avgServeTime = lastDayPerformance.getAvg_served_time();

        widgets.setQueueLength(queue_length);
        widgets.setQueueLength_last_hour(queue_length_last_hour);
        widgets.setTotal_served_customers(total_served_customers);
        widgets.setTotal_served_customers_last_hour(served_customers_last_hour);
        widgets.setAvg_serve_time(avgServeTime);
        widgets.setAvg_wait_time(avgWaitTime);

        QueueDetailsDTO details = queueService.getQueueDetails(queueId);

        MetricsDTO metrics = new MetricsDTO();

        metrics.setQueueDetails(details);
        metrics.setWidgets(widgets);
        metrics.setQueueWeeklyPerformance(weekly_performance);
        metrics.setQueueMonthlyPerformance(monthly_performance);

        return ResponseEntity.ok(metrics);

    }


}
