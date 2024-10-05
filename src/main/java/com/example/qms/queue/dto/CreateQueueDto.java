package com.example.qms.queue.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateQueueDto {
    @NotBlank(message = "Title is mandatory")
    private String title;
}
