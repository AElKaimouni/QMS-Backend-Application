package com.example.qms.reservation;

import com.example.qms.reservation.dto.ConsultReservationStateDTO;
import com.example.qms.reservation.dto.CreateReservationDTO;
import com.example.qms.reservation.dto.ReservationDTO;
import com.example.qms.reservation.dto.ReservationInfoDTO;
import com.example.qms.reservation.exceptions.ResrvationNotFoundException;
import com.example.qms.reservation.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    ReservationService reservationService;

    //Create Reservation
    @PostMapping
    public ResponseEntity<ReservationDTO> createReservation(@RequestBody CreateReservationDTO createReservationDTO) {
        // Call the service to create the reservation
        ReservationDTO createdReservation = reservationService.createReservation(createReservationDTO);
        // Return the created reservation as a response
        return ResponseEntity.ok(createdReservation);
    }

    // Get a single reservation by ID
    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable int id) {
        ReservationDTO reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(reservation);
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
            ReservationInfoDTO info = reservationService.consultReservation(dto);

            return ResponseEntity.ok(info);
        } catch (ResrvationNotFoundException e) {

            return ResponseEntity.notFound().build();
        }
    }
}
