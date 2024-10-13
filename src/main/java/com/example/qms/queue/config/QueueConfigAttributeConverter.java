package com.example.qms.queue.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class QueueConfigAttributeConverter implements AttributeConverter<QueueConfig, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(QueueConfig config) {
        try {
            return objectMapper.writeValueAsString(config);
        } catch (JsonProcessingException jpe) {
            //log.warn("Cannot convert Address into JSON");
            return null;
        }
    }

    @Override
    public QueueConfig convertToEntityAttribute(String value) {
        try {
            return objectMapper.readValue(value, QueueConfig.class);
        } catch (JsonProcessingException e) {
            //log.warn("Cannot convert JSON into Address");
            return null;
        }
    }
}