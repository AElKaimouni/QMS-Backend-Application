package com.example.qms.reservation.services;

import com.example.qms.queue.Queue;
import com.example.qms.queue.QueueRepository;
import com.example.qms.reservation.Reservation;
import com.example.qms.reservation.ReservationRepository;
import com.example.qms.reservation.dto.ConsultReservationStateDTO;
import com.example.qms.reservation.dto.CreateReservationDTO;
import com.example.qms.reservation.dto.ReservationDTO;
import com.example.qms.reservation.dto.ReservationInfoDTO;
import com.example.qms.reservation.exceptions.ReservationNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    @Autowired
    ReservationRepository reservationRepository;
    @Autowired
    QueueRepository queueRepository;

    private ReservationDTO mapToDTO(Reservation reservation) {
        return new ReservationDTO(reservation);
    }

    public ReservationDTO createReservation(CreateReservationDTO createReservationDTO, int position, String token) {
        // Create and configure a new Reservation object
        Reservation reservation = new Reservation();

        reservation.setToken(token);
        reservation.setPosition(position);
        reservation.setEmail(createReservationDTO.getEmail());
        reservation.setJoinAt(Timestamp.valueOf(LocalDateTime.now()));
        reservation.setQueueId(createReservationDTO.getQueueId());
        

        // Save the reservation to the database
        reservationRepository.save(reservation);

        return mapToDTO(reservation);
    }

    // Read a single reservation by ID
    public Optional<Reservation> getReservation(int id) {

        return reservationRepository.findById(id);
    }

    // Read all reservations
    public List<ReservationDTO> getAllReservationsForQueue(UUID queueId) {
        // Fetch all reservations for the queue by its ID
        List<Reservation> reservations = reservationRepository.findAllByQueueId(queueId);
        return reservations.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // Delete reservation
    public void deleteReservation(int id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found with id: " + id));
        reservationRepository.delete(reservation);
    }

    public Reservation getReservation(UUID queueId, int position) throws ReservationNotFoundException {
        Optional<Reservation> reservation = reservationRepository.findByQueueIdAndPosition(queueId, position);

        if(reservation.isEmpty()) throw new ReservationNotFoundException();

        return reservation.get();
    }

    public void saveReservation(Reservation reservation) {
        this.reservationRepository.save(reservation);
    }
}
