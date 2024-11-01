package com.example.qms.queue;

import com.example.qms.queue.dto.CreateQueueDTO;
import com.example.qms.queue.dto.QueueConsultationInfoDTO;
import com.example.qms.queue.dto.QueueDTO;
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
import com.example.qms.user.services.UserService;
import com.example.qms.workspace.exceptions.WorkspaceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private UserService userService;

    @Autowired
    private QueueService queueService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ReservationService reservationService;

    @GetMapping("/{workspace_id}/all")
    public List<QueueDTO> getAllQueues(
        @PathVariable("workspace_id") long workspaceId
    ) {
        CustomUserDetails userDetails = userService.auth();
        Long userId = userDetails.getId();

        return queueService.getQueues(userId, workspaceId);
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
    public ResponseEntity<QueueDTO> getQueue(@PathVariable UUID queueId) {
        Optional<Queue> queue = queueService.getQueue(queueId);

        if(queue.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        QueueDTO dto = new QueueDTO(queue.get());

        return ResponseEntity.ok(dto);
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
    public ResponseEntity<?> createQueue(
            @Valid @RequestBody CreateQueueDTO dto,
            @RequestParam("wid") long workspaceId
    ) {
        try {
            CustomUserDetails userDetails = userService.auth();
            long userId = userDetails.getId();

            QueueDTO queue = queueService.createQueue(dto, userId, workspaceId);

            return ResponseEntity.ok(queue);
        } catch (WorkspaceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{queueId}/next")
    public ResponseEntity<Void> next(@PathVariable UUID queueId) {
        Queue queue; int newPosition;

        CustomUserDetails userDetails = userService.auth();
        Long userId = userDetails.getId();

        try {

            // Fetch the queue
            queue = queueService.getMustExistQueue(queueId);
            Long queueOwnerId = queue.getUserId();

            // Check if the authenticated user is the owner of the queue
            if (!userId.equals(queueOwnerId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden if not the owner
            }

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
        CustomUserDetails userDetails = userService.auth();
        Long userId = userDetails.getId();

        Queue queue;
        try {
            queue = queueService.getMustExistQueue(queueId);
            Long queueOwnerId = queue.getUserId();

            if (!userId.equals(queueOwnerId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden if not the owner
            }

            queueService.delete(queueId);
            return ResponseEntity.ok().build();
        } catch (QueueNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @PostMapping("/{queueId}/start")
    public ResponseEntity<Void> start(@PathVariable UUID queueId) {
        CustomUserDetails userDetails = userService.auth();
        Long userId = userDetails.getId();

        Queue queue;
        try {
            queue = queueService.getMustExistQueue(queueId);
            Long queueOwnerId = queue.getUserId();

            if (!userId.equals(queueOwnerId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden if not the owner
            }

            queueService.start(queueId);
            return ResponseEntity.ok().build();
        } catch (QueueNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @PostMapping("/{queueId}/pause")
    public ResponseEntity<Void> pause(@PathVariable UUID queueId) {
        CustomUserDetails userDetails = userService.auth();
        Long userId = userDetails.getId();

        Queue queue;
        try {
            queue = queueService.getMustExistQueue(queueId);
            Long queueOwnerId = queue.getUserId();

            if (!userId.equals(queueOwnerId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden if not the owner
            }

            queueService.paused(queueId);
            return ResponseEntity.ok().build();
        } catch (QueueNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @PostMapping("/{queueId}/close")
    public ResponseEntity<Void> close(@PathVariable UUID queueId) {
        CustomUserDetails userDetails = userService.auth();
        Long userId = userDetails.getId();

        Queue queue;
        try {
            queue = queueService.getMustExistQueue(queueId);
            Long queueOwnerId = queue.getUserId();

            if (!userId.equals(queueOwnerId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden if not the owner
            }

            queueService.close(queueId);
            return ResponseEntity.ok().build();
        } catch (QueueNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


}