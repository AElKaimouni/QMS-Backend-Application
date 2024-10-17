package com.example.qms.reservation;

import com.example.qms.queue.Queue;
import com.example.qms.queue.services.QueueService;
import com.example.qms.reservation.dto.ConsultReservationStateDTO;
import com.example.qms.reservation.dto.CreateReservationDTO;
import com.example.qms.reservation.dto.ReservationDTO;
import com.example.qms.reservation.dto.ReservationInfoDTO;
import com.example.qms.reservation.exceptions.ReservationNotFoundException;
import com.example.qms.reservation.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    //Create Reservation
    @PostMapping
    public ResponseEntity<ReservationDTO> createReservation(@RequestBody CreateReservationDTO createReservationDTO) {
        // Get the next available position from the queue system
        int queuePosition = queueService.reserve(createReservationDTO.getQueueId());
        // Get the token from the queue service
        String token = queueService.generateToken(queuePosition,createReservationDTO.getQueueId());
        // Call the service to create the reservation
        ReservationDTO createdReservation = reservationService.createReservation(createReservationDTO, queuePosition, token);
        // Return the created reservation as a response
        return ResponseEntity.ok(createdReservation);
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
    public ResponseEntity<List<ReservationDTO>> getAllReservationsByQueueId(@PathVariable UUID queueId) {
        List<ReservationDTO> reservations = reservationService.getAllReservationsForQueue(queueId);
        return ResponseEntity.ok(reservations);
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
