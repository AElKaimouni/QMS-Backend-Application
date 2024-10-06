package com.example.qms.queue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import com.example.qms.queue.Queue;

@Data
public class CreateQueueRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private int length = 0;
}
