package com.example.qms.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationInfoDTO {
    private String queueTitle;
    
    private int position;
    private int counter;
    private int estimatedWaitTime;
}
