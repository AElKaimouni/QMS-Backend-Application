package com.example.qms.queue.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateQueueDto {
    @NotBlank(message = "Title is required")
    private String title;

    private int length = 0;
}
