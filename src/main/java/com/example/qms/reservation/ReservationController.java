package com.example.qms.reservation;

import com.example.qms.queue.Queue;
import com.example.qms.queue.exceptions.QueueNotFoundException;
import com.example.qms.queue.services.QueueService;
import com.example.qms.reservation.dto.ConsultReservationStateDTO;
import com.example.qms.reservation.dto.CreateReservationDTO;
import com.example.qms.reservation.dto.ReservationDTO;
import com.example.qms.reservation.dto.ReservationInfoDTO;
import com.example.qms.reservation.exceptions.ReservationNotFoundException;
import com.example.qms.reservation.services.ReservationService;
import com.example.qms.utils.EmailService;
import com.example.qms.utils.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    ReservationService reservationService;
    @Autowired
    QueueService queueService;
    @Autowired
    EmailService emailService;

    @Autowired
    private PdfService pdfService;

    @GetMapping("/generate-pdf/{reservation_id}")
    public ResponseEntity<byte[]> generatePdf(@PathVariable("reservation_id") int reservationId) {
        Optional<Reservation> reservation = reservationService.getReservation(reservationId);

        if(reservation.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        Queue queue = queueService.getMustExistQueue(reservation.get().getQueueId());

        byte[] pdfBytes = pdfService.generatePdf(
            reservation.get().getPosition(),
            queue.getTitle(),
            reservation.get().getToken()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=document.pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    //Create Reservation
    @PostMapping
    public ResponseEntity<ReservationDTO> createReservation(@RequestBody CreateReservationDTO createReservationDTO) throws Exception {
        try {
            Queue queue = queueService.getMustExistQueue(createReservationDTO.getQueueId());
            double estimatedServeTime = queueService.getAverageServingTime(createReservationDTO.getQueueId());
            int queuePosition = queueService.reserve(createReservationDTO.getQueueId());
            String token = queueService.generateToken(queuePosition,createReservationDTO.getQueueId());
            ReservationDTO createdReservation = reservationService.createReservation(createReservationDTO, queuePosition, token);

            // notify by email
            emailService.sendReservationEmail(
                createdReservation.getEmail(),
                createdReservation.getToken(),
                queue.getTitle(),
                "",
                queuePosition,
                queue.getCounter(),
                new Date((new Date()).getTime() + (int) estimatedServeTime * 1000)
            );

            // Return the created reservation as a response
            return ResponseEntity.ok(createdReservation);
        } catch (QueueNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Get a single reservation by ID
    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable int id) {
        Optional<Reservation> reservation = reservationService.getReservation(id);

        if(reservation.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        ReservationDTO dto = new ReservationDTO(reservation.get());


        return ResponseEntity.ok(dto);
    }

    // Get all reservations for a queue
    @GetMapping("/queue/{queueId}")
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

    //Delete Reservation
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable int id) {
        // Appel du service pour supprimer la réservation
        reservationService.deleteReservation(id);
        // Retourner un statut 204 (No Content) si la suppression a réussi
        return ResponseEntity.noContent().build();
    }

    // Consult Reservation
    @GetMapping("/{id}/consult")
    public ResponseEntity<ReservationInfoDTO> consultReservation(@PathVariable int id) {
        try {
            ConsultReservationStateDTO dto = new ConsultReservationStateDTO(id);
            // Fetch the Reservation instance using its ID
            Optional<Reservation> reservation = reservationService.getReservation(dto.getReservationID());

            if(reservation.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

            // Fetch the Queue instance using the queueId from the Reservation
            Optional<Queue> queue = queueService.getQueue(reservation.get().getQueueId());

            if(queue.isEmpty()) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

            // Create and return the new DTO with the necessary properties
            ReservationInfoDTO info = new ReservationInfoDTO(
                    reservation.get().getPosition(),
                    queue.get().getLength(),
                    queue.get().getCounter()
            );

            return ResponseEntity.ok(info);
        } catch (ReservationNotFoundException e) {

            return ResponseEntity.notFound().build();
        }
    }
}
