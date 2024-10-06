package com.example.qms.reservation.services;

import com.example.qms.queue.Queue;
import com.example.qms.queue.QueueRepository;
import com.example.qms.queue.services.QueueServiceInterface;
import com.example.qms.reservation.Reservation;
import com.example.qms.reservation.ReservationRepository;
import com.example.qms.reservation.dto.CreateReservationDTO;
import com.example.qms.reservation.dto.ReservationDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    @Autowired
    ReservationRepository reservationRepository;
    @Autowired
    QueueServiceInterface queueService;
    @Autowired
    QueueRepository queueRepository;

    /**
     * Maps a Reservation entity to a ReservationDTO.
     *
     * @param reservation the Reservation entity to be mapped
     * @return a ReservationDTO with relevant fields
     */
    private ReservationDTO mapToDTO(Reservation reservation) {
        return new ReservationDTO(
                reservation.getId(),
                reservation.getToken(),
                reservation.getPosition(),
                reservation.getEmail(),
                reservation.getStatus(),
                reservation.getJoinAt(),
                (reservation.getQueue()).getId()
        );
    }

    public ReservationDTO createReservation(CreateReservationDTO createReservationDTO) {
        // Get the next available position from the queue system
        int queuePosition = queueService.reserve();
        // Get the token from the queue service
        String token = queueService.generateToken();

        Queue queue = queueRepository.findById(createReservationDTO.getQueueId())
                .orElseThrow(() -> new EntityNotFoundException("Queue not found"));

        // Create and configure a new Reservation object
        Reservation reservation = new Reservation();
        reservation.setToken(token);  // Set token from queue service
        reservation.setPosition(queuePosition);  // Assign position from queue service
        reservation.setEmail(createReservationDTO.getEmail());
        reservation.setQueue(queue); // Set the queue
        reservation.setStatus("PENDING");
        reservation.setJoinAt(Timestamp.valueOf(LocalDateTime.now())); // Définir la date/heure actuelle

        // Save the reservation to the database
        reservationRepository.save(reservation);
        return mapToDTO(reservation);
    }

    // Read a single reservation by ID
    public ReservationDTO getReservationById(UUID id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found with id: " + id));
        return mapToDTO(reservation);
    }

    // Read all reservations
    public List<ReservationDTO> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream().map(this::mapToDTO).collect(Collectors.toList());
    }
    // Get all reservations for the day
    public List<ReservationDTO> getReservationsForDay(LocalDateTime date) {
        LocalDateTime endOfDay = date.plusDays(1).minusNanos(1); // get the end of the day
        List<Reservation> reservations = reservationRepository.findAllByJoinAtBetween(Timestamp.valueOf(date), Timestamp.valueOf(endOfDay));
        return reservations.stream()
                .map(this::mapToDTO) // Ensure proper mapping to DTO
                .collect(Collectors.toList());
    }

    // Get all reservations for a specific hour
    public List<ReservationDTO> getReservationsForHour(LocalDateTime hour) {
        LocalDateTime startOfHour = hour.withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfHour = hour.withMinute(59).withSecond(59).withNano(999999999);
        List<Reservation> reservations = reservationRepository.findAllByJoinAtBetween(Timestamp.valueOf(startOfHour), Timestamp.valueOf(endOfHour));
        return reservations.stream().map(this::mapToDTO) // Ensure proper mapping to DTO
                                    .collect(Collectors.toList());
    }

    // Delete reservation
    public void deleteReservation(UUID id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found with id: " + id));
        reservationRepository.delete(reservation);
    }
}
