package com.example.qms.reservation.dto;

import com.example.qms.queue.config.QueueConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateReservationDTO {
        private UUID queueId;
        private String email;
        private Map<String, Object> info;
}
