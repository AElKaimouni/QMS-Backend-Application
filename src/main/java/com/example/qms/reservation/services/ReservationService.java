package com.example.qms.reservation.services;

import com.example.qms.queue.Queue;
import com.example.qms.queue.QueueRepository;
import com.example.qms.queue.services.QueueService;
import com.example.qms.queue.services.QueueServiceInterface;
import com.example.qms.reservation.Reservation;
import com.example.qms.reservation.ReservationRepository;
import com.example.qms.reservation.dto.ConsultReservationStateDTO;
import com.example.qms.reservation.dto.CreateReservationDTO;
import com.example.qms.reservation.dto.ReservationDTO;
import com.example.qms.reservation.dto.ReservationInfoDTO;
import com.example.qms.reservation.exceptions.ResrvationNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    @Autowired
    ReservationRepository reservationRepository;
    @Autowired
    QueueService queueService;
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
        int queuePosition = queueService.reserve(createReservationDTO.getQueueId());
        // Get the token from the queue service
        String token = queueService.generateToken(queuePosition,createReservationDTO.getQueueId());

        Queue queue = queueRepository.findById(createReservationDTO.getQueueId())
                .orElseThrow(() -> new EntityNotFoundException("Queue not found"));

        // Create and configure a new Reservation object
        Reservation reservation = new Reservation();
        reservation.setToken(token);  // Set token from queue service
        reservation.setPosition(queuePosition);  // Assign position from queue service
        reservation.setEmail(createReservationDTO.getEmail());
        reservation.setQueue(queue); // Set the queue
        reservation.setJoinAt(Timestamp.valueOf(LocalDateTime.now())); // Définir la date/heure actuelle

        // Save the reservation to the database
        reservationRepository.save(reservation);
        return mapToDTO(reservation);
    }

    // Read a single reservation by ID
    public ReservationDTO getReservationById(int id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found with id: " + id));
        return mapToDTO(reservation);
    }

    // Read all reservations
    public List<ReservationDTO> getAllReservationsForQueue(UUID queueId) {
    // Fetch all reservations for the queue by its ID
        Optional<Queue> queue = queueRepository.findById(queueId);
        List<Reservation> reservations = reservationRepository.findAllByQueue(queue);
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
    public void deleteReservation(int id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found with id: " + id));
        reservationRepository.delete(reservation);
    }

    public ReservationInfoDTO consultReservation(ConsultReservationStateDTO consultReservationDTO) throws ResrvationNotFoundException {
        // Fetch the Reservation instance using its ID
        Optional<Reservation> reservation = reservationRepository.findById(consultReservationDTO.getReservationID());

        if(reservation.isEmpty()) throw new ResrvationNotFoundException();

        // Fetch the Queue instance using the queueId from the Reservation
        Queue queue = reservation.get().getQueue();

        // Create and return the new DTO with the necessary properties
        return new ReservationInfoDTO(
                reservation.get().getPosition(),
                queue.getLength(),
                queue.getCounter()
        );
    }
}
