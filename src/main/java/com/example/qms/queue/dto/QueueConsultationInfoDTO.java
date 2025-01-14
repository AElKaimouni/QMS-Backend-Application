package com.example.qms.queue.dto;

import com.example.qms.queue.Queue;
import com.example.qms.queue.config.QueueConfig;
import com.example.qms.queue.enums.QueueStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Data
@NoArgsConstructor
public class QueueConsultationInfoDTO {
    private UUID id;
    private String title;
    private String description;
    private int counter = 0; // current position of the queue
    private int length;
    private QueueStatus status;
    private QueueConfig config;

    private int averageServeTime;

    public QueueConsultationInfoDTO(Queue queue, double averageServeTime) {
        this.setId(queue.getId());
        this.setTitle(queue.getTitle());
        this.setDescription(queue.getDescription());
        this.setLength(queue.getLength());
        this.setCounter(queue.getCounter());
        this.setStatus(queue.getStatus());
        this.setConfig(queue.getConfig());

        // to-do: get reservations estimated time
        this.setAverageServeTime((int) (averageServeTime * 1000));
    }
}
