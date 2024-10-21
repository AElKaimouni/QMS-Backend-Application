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

    // generate pdf ticket
    @GetMapping("/generate-pdf/{reservation_id}")
    public ResponseEntity<byte[]> generatePdf(
        @PathVariable("reservation_id") int reservationId,
        @RequestParam("token") String token
    ) {
        Optional<Reservation> reservation = reservationService.getReservation(reservationId);

        if(reservation.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        if(!reservation.get().getToken().equals(token)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        Queue queue = queueService.getMustExistQueue(reservation.get().getQueueId());

        byte[] pdfBytes = pdfService.generatePdf(
            reservationId,
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
    public ResponseEntity<ReservationDTO> createReservation(
            @RequestBody CreateReservationDTO createReservationDTO
    ) throws Exception {
        try {
            Queue queue = queueService.getMustExistQueue(createReservationDTO.getQueueId());
            double estimatedServeTime = queueService.getAverageServingTime(createReservationDTO.getQueueId());
            int queuePosition = queueService.reserve(createReservationDTO.getQueueId());
            String token = queueService.generateToken(queuePosition,createReservationDTO.getQueueId());
            ReservationDTO createdReservation = reservationService.createReservation(createReservationDTO, queuePosition, token);
            String ticketLink = reservationService.generateReservationTicketURL(
                createdReservation.getId(),
                createdReservation.getToken()
            );

            // notify by email
            emailService.sendReservationEmail(
                createdReservation.getEmail(),
                createdReservation.getToken(),
                queue.getTitle(),
                "",
                ticketLink,
                createdReservation.getId(),
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

    // Consult Reservation
    @GetMapping("/{id}/consult")
    public ResponseEntity<ReservationInfoDTO> consultReservation(
            @PathVariable("id") int id,
            @RequestParam("token") String token
    ) {
        try {
            Optional<Reservation> reservation = reservationService.getReservation(id);

            if(reservation.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            if(!reservation.get().getToken().equals(token)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

            // Fetch the Queue instance using the queueId from the Reservation
            Queue queue = queueService.getMustExistQueue(reservation.get().getQueueId());
            double averageServTime = queueService.getAverageServingTime(reservation.get().getQueueId());
            int esitmatedWaitTime = (int) (averageServTime * (queue.getLength() - queue.getCounter()));

            // Create and return the new DTO with the necessary properties
            ReservationInfoDTO info = new ReservationInfoDTO(
                queue.getTitle(),
                reservation.get().getPosition(),
                queue.getCounter(),
                esitmatedWaitTime
            );

            return ResponseEntity.ok(info);
        } catch (ReservationNotFoundException e) {

            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity cancelReservation(
        @PathVariable("id") long reservationId,
        @RequestParam("token") String token
    ) {
        Optional<Reservation> reservation = reservationService.getReservation(reservationId);

        if(reservation.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        if(!reservation.get().getToken().equals(token)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        try {
            reservationService.cancelReservation(reservationId);

            return ResponseEntity.ok().build();
        } catch (ReservationNotFoundException e) {
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
