package com.example.qms.queue.dto;

import com.example.qms.queue.config.QueueConfig;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data

public class CreateQueueDTO {
    @NotNull(message = "Title is required")
    @NotBlank(message = "Title is required")
    private String title;
    private String description = "";

    private QueueConfig config;
}
