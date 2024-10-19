package com.example.qms.queue.dto;

import com.example.qms.queue.config.QueueConfig;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data

public class CreateQueueDTO {
    @NotBlank(message = "Title is required")
    private String title;
    private String description = "";

    private QueueConfig config;
}
