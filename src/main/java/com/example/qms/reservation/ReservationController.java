package com.example.qms.reservation;

import com.example.qms.reservation.dto.CreateReservationDTO;
import com.example.qms.reservation.dto.ReservationDTO;
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
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable UUID id) {
        ReservationDTO reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(reservation);
    }
    // Get all reservations
    @GetMapping
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        List<ReservationDTO> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }

    // Get reservations for a specific day (YYYY-MM-DD)
    @GetMapping("/day/{date}")
    public ResponseEntity<List<ReservationDTO>> getReservationsForDay(@PathVariable String date) {
        LocalDate parsedDate = LocalDate.parse(date);  // Example: "2024-10-06"
        List<ReservationDTO> reservations = reservationService.getReservationsForDay(parsedDate.atStartOfDay());
        return ResponseEntity.ok(reservations);
    }
    // Get reservations for a specific hour (YYYY-MM-DDTHH format)
    @GetMapping("/hour/{date}/{hour}")
    public ResponseEntity<List<ReservationDTO>> getReservationsForHour(@PathVariable String date, @PathVariable String hour) {
        LocalDate parsedDate = LocalDate.parse(date);  // Example: "2024-10-06"
        LocalTime parsedTime = LocalTime.parse(hour + ":00:00");  // Example: "14" -> "14:00:00"
        List<ReservationDTO> reservations = reservationService.getReservationsForHour(parsedDate.atTime(parsedTime));
        return ResponseEntity.ok(reservations);
    }

    //Delete Reservation
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable UUID id) {
        // Appel du service pour supprimer la réservation
        reservationService.deleteReservation(id);
        // Retourner un statut 204 (No Content) si la suppression a réussi
        return ResponseEntity.noContent().build();
    }




}
