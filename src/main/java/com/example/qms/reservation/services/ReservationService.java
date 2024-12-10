package com.example.qms.reservation.services;

import com.example.qms.queue.Queue;
import com.example.qms.queue.QueueRepository;
import com.example.qms.reservation.Reservation;
import com.example.qms.reservation.ReservationRepository;
import com.example.qms.reservation.dto.ConsultReservationStateDTO;
import com.example.qms.reservation.dto.CreateReservationDTO;
import com.example.qms.reservation.dto.ReservationDTO;
import com.example.qms.reservation.dto.ReservationInfoDTO;
import com.example.qms.reservation.enums.ReservationStatus;
import com.example.qms.reservation.exceptions.ReservationNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
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

    @Value("${spring.app.url}")
    private String appURL;

    public static final int MAX_PAGE_SIZE = 50;

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
        reservation.setInfo(new JSONObject(createReservationDTO.getInfo()));

        // Save the reservation to the database
        reservationRepository.save(reservation);

        return mapToDTO(reservation);
    }

    // Read a single reservation by ID
    public Optional<Reservation> getReservation(long id) {

        return reservationRepository.findById(id);
    }

    public List<ReservationDTO> getRecentReservationsForUser(long user_id, int limit) {
        return reservationRepository.findLastNReservationsByUserId(user_id, limit)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // Read all reservations
    public Page<ReservationDTO> getAllReservationsForQueue(UUID queueId, int page, int size) {
        if (size > MAX_PAGE_SIZE) {
            size = MAX_PAGE_SIZE;
        }

        Page<Reservation> reservationsPage = reservationRepository.findByQueueId(
            queueId,
            PageRequest.of(page, size, Sort.by("id").ascending())
        );

        List<ReservationDTO> list = reservationsPage.stream().map(this::mapToDTO).collect(Collectors.toList());

        return new PageImpl<>(list, reservationsPage.getPageable(), reservationsPage.getTotalElements());
    }

    // get all current reservations
    public Page<ReservationDTO> getAllCurrentReservations(UUID queueId, int page, int size) {
        if (size > MAX_PAGE_SIZE) {
            size = MAX_PAGE_SIZE;
        }

        Page<Reservation> reservationsPage = reservationRepository
            .findByQueueIdAndStatusIn(
                queueId,
                Arrays.asList("WAITING", "SERVING"),
                PageRequest.of(page, size, Sort.by("id").ascending())
            );

        List<ReservationDTO> list = reservationsPage.stream().map(this::mapToDTO).collect(Collectors.toList());

        return new PageImpl<>(list, reservationsPage.getPageable(), reservationsPage.getTotalElements());
    }

    // get all past reservations
    public Page<ReservationDTO> getAllPastReservations(UUID queueId, int page, int size) {
        if (size > MAX_PAGE_SIZE) {
            size = MAX_PAGE_SIZE;
        }

        Page<Reservation> reservationsPage = reservationRepository
            .findByQueueIdAndStatusNotIn(
                queueId,
                Arrays.asList("WAITING", "SERVING"),
                PageRequest.of(page, size, Sort.by("id").descending())
            );

        List<ReservationDTO> list = reservationsPage.stream().map(this::mapToDTO).collect(Collectors.toList());

        return new PageImpl<>(list, reservationsPage.getPageable(), reservationsPage.getTotalElements());
    }

    // Delete reservation
    public void deleteReservation(long id) {
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

    public void cancelReservation(long reservationId) {
        Optional<Reservation> reservation = reservationRepository.findById(reservationId);

        if(reservation.isEmpty()) throw new ReservationNotFoundException();

        reservation.get().setStatus(ReservationStatus.CANCELED);

        reservationRepository.save(reservation.get());
    }

    public String generateReservationTicketURL(long reservationId, String token) throws UnsupportedEncodingException {
        String encodedToken = URLEncoder.encode(token, "UTF-8");
        return appURL + "/reservations/generate-pdf/" + reservationId + "?token=" + encodedToken;
    }

    public String generateReservationConsultantURL(long reservationId, UUID queueID, String token) throws UnsupportedEncodingException {
        String encodedToken = URLEncoder.encode(token, "UTF-8");

        return appURL + "/" + queueID.toString() + "/reservations/" + reservationId + "?token=" + encodedToken;
    }

    // Method to count reservations by userId, status, and specific date range
    public long countReservations(Long userId, ReservationStatus status, Timestamp startDate, Timestamp endDate) {
        if (status != null) {
            return reservationRepository.countReservationsByUserIdAndStatusAndDateRange(userId, status, startDate, endDate);
        } else {
            return reservationRepository.countReservationsByUserIdAndDateRange(userId, startDate, endDate);
        }
    }

    public long countReservations(Long userId, Timestamp startDate, Timestamp endDate) {
        return countReservations(userId, null, startDate, endDate);
    }

    public long getTodayReservationsCount(Long userId, ReservationStatus status) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = now.toLocalDate().atTime(LocalTime.MAX);

        return countReservations(userId, status, Timestamp.valueOf(startOfDay), Timestamp.valueOf(endOfDay));
    }

    public long getTodayReservationsCount(Long userId) {
        return getTodayReservationsCount(userId, null);
    }

    public long getYesterdayReservationsCount(Long userId, ReservationStatus status) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfYesterday = now.minusDays(1).toLocalDate().atStartOfDay();
        LocalDateTime endOfYesterday = now.minusDays(1).toLocalDate().atTime(LocalTime.MAX);

        return countReservations(userId, status, Timestamp.valueOf(startOfYesterday), Timestamp.valueOf(endOfYesterday));
    }

    public long getYesterdayReservationsCount(Long userId) {
        return getYesterdayReservationsCount(userId, null);
    }

    public long getLastHourReservationsCount(Long userId, ReservationStatus status) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfLastHour = now.minusHours(1);
        LocalDateTime endOfLastHour = now;

        return countReservations(userId, status, Timestamp.valueOf(startOfLastHour), Timestamp.valueOf(endOfLastHour));
    }

    public long getLastHourReservationsCount(Long userId) {
        return getLastHourReservationsCount(userId, null);
    }

}
