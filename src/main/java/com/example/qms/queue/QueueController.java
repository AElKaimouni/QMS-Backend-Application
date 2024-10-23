package com.example.qms.queue;

import com.example.qms.queue.dto.CreateQueueDTO;
import com.example.qms.queue.dto.QueueConsultationInfoDTO;
import com.example.qms.queue.exceptions.QueueCounterLimitException;
import com.example.qms.queue.exceptions.QueueNotFoundException;
import com.example.qms.queue.services.QueueService;
import com.example.qms.reservation.Reservation;
import com.example.qms.reservation.dto.ReservationDTO;
import com.example.qms.reservation.enums.ReservationStatus;
import com.example.qms.reservation.exceptions.ReservationNotFoundException;
import com.example.qms.reservation.services.ReservationService;
import com.example.qms.user.User;
import com.example.qms.user.config.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.server.ResponseStatusException;
import com.example.qms.utils.EmailService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/queue")
public class QueueController {

    @Autowired
    private QueueService queueService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ReservationService reservationService;

    @GetMapping("/all")
    public List<Queue> getAllQueues() {
        // In your code (for example, in a controller):
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();  // Now you can access the user ID
        return queueService.getQueues(userId);
    }

    @GetMapping("/{qid}/validate/{token}")
    public Integer validateToken(
            @PathVariable("qid") String qid,
            @PathVariable("token") String token
    ) {
        // get Queue Secret
        String queueSecret = queueService.getQueueSecret(qid);

        // string to uuid
        UUID queueId = UUID.fromString(qid);

        return queueService.validateToken(token, queueId);
    }

    @GetMapping("/{queueId}")
    public ResponseEntity<Queue> getQueue(@PathVariable UUID queueId) {
        Optional<Queue> queue = queueService.getQueue(queueId);

        if(queue.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        return ResponseEntity.ok(queue.get());
    }

    @GetMapping("/{queueId}/consult")
    public ResponseEntity<QueueConsultationInfoDTO> consultQueue(@PathVariable UUID queueId) {
        Optional<Queue> queue = queueService.getQueue(queueId);

        if(queue.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        double averageServTime = queueService.getAverageServingTime(queueId);
        QueueConsultationInfoDTO res = new QueueConsultationInfoDTO(queue.get(), averageServTime);

        return ResponseEntity.ok(res);
    }

    @PostMapping
    public ResponseEntity<String> createQueue(
            @Valid @RequestBody CreateQueueDTO dto
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();

        String queueId = queueService.createQueue(dto, userId);

        return ResponseEntity.ok(queueId);
    }

    @PostMapping("/{queueId}/next")
    public ResponseEntity<Void> next(@PathVariable UUID queueId) {
        Queue queue; int newPosition;
        try {
            queue = queueService.next(queueId);
            newPosition = queue.getCounter();
        } catch (QueueNotFoundException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (QueueCounterLimitException e) {

            queue = e.queue;
            newPosition = queue.getCounter() + 1;
        }

        int queueLength = queue.getLength();

        try {
            // change the prev reservation status to SERVED
            if(newPosition > 1) {
                Reservation reservation = reservationService.getReservation(queueId, newPosition - 1);
                reservation.setStatus(ReservationStatus.SERVED);
                reservation.setServedAt(Timestamp.valueOf(LocalDateTime.now()));
                reservationService.saveReservation(reservation);
            }

            // change the next reservation status to SERVING
            if(queueLength >= newPosition) {
                Reservation nextReservation = reservationService.getReservation(queueId, newPosition);
                nextReservation.setStatus(ReservationStatus.SERVING);
                nextReservation.setCalledAt(Timestamp.valueOf(LocalDateTime.now()));

                // send notification mail
                this.emailService.sendReservationArrivedEmail(nextReservation.getEmail(), "client name");

                reservationService.saveReservation(nextReservation);
            }
        } catch (ReservationNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok().build();
    }

    // Get all reservations for a queue
    @GetMapping("/{queueId}/reservations")
    public ResponseEntity<Page<ReservationDTO>> getCurrentReservations(
            @PathVariable UUID queueId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "all") String scope
    ) {
        switch (scope) {
            case "current": {
                Page<ReservationDTO> reservations = reservationService.getAllCurrentReservations(queueId, page, size);
                return ResponseEntity.ok(reservations);
            }
            case "past": {
                Page<ReservationDTO> reservations = reservationService.getAllPastReservations(queueId, page, size);
                return ResponseEntity.ok(reservations);
            }
            default: {
                Page<ReservationDTO> reservations = reservationService.getAllReservationsForQueue(queueId, page, size);
                return ResponseEntity.ok(reservations);
            }
        }

    }

    @DeleteMapping("/{queueId}")
    public ResponseEntity<Void> delete(@PathVariable UUID queueId) {
        queueService.delete(queueId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{queueId}/start")
    public ResponseEntity<Void> start(@PathVariable UUID queueId) {
        queueService.start(queueId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{queueId}/pause")
    public ResponseEntity<Void> pause(@PathVariable UUID queueId) {
        queueService.paused(queueId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{queueId}/close")
    public ResponseEntity<Void> close(@PathVariable UUID queueId) {
        queueService.close(queueId);
        return ResponseEntity.ok().build();
    }

}