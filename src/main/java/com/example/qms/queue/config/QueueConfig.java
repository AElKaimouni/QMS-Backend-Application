package com.example.qms.queue.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueueConfig {
    private QueueConfigTime time;
    private List<QueueConfigField> fields;
}
