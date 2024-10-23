package com.example.qms.queue.config;

import com.example.qms.queue.enums.QueueConfigFieldType;
import lombok.Data;

@Data
public class QueueConfigField {
    private String name;
    private QueueConfigFieldType type;
    private boolean required;
}
