package com.example.qms.queue.config;

import lombok.Data;

@Data
public class QueueConfigField {
    private String name;
    private String type;
    private boolean required;
}
