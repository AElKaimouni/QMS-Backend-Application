package com.example.qms.queue.dto;

import com.example.qms.queue.enums.QueueStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueueDetailsDTO {
    UUID id;
    String workspaceName;
    String title;
    QueueStatus status;
    String description;

}
