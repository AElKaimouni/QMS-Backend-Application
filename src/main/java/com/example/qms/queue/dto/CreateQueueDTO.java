package com.example.qms.queue.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateQueueDTO {
    @NotBlank(message = "Title is required")
    private String title;
    private String description = "";
    private int length = 0;
}
