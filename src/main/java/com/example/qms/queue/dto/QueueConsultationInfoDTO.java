package com.example.qms.queue.dto;

import com.example.qms.queue.Queue;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Data
public class QueueConsultationInfoDTO extends Queue {
    @JsonIgnore
    private LocalDateTime updatedAt;
    private int averageServeTime;

    public QueueConsultationInfoDTO(Queue queue, double averageServeTime) {
        this.setId(queue.getId());
        this.setTitle(queue.getTitle());
        this.setDescription(queue.getDescription());
        this.setLength(queue.getLength());
        this.setCounter(queue.getCounter());
        this.setStatus(queue.getStatus());
        this.setCreatedAt(queue.getCreatedAt());

        // to-do: get reservations estimated time
        this.setAverageServeTime((int) (averageServeTime * 1000));
    }
}
