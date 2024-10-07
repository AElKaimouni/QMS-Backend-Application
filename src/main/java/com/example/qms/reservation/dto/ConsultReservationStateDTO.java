package com.example.qms.reservation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsultReservationStateDTO {
    @NotNull(message = "Reservation ID is required")
    private int reservationID;
}
