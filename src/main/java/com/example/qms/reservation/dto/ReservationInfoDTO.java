package com.example.qms.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationInfoDTO {
    private int reservationPosition;
    private int queueCounter;
    private int estimatedWaitTime;
}
