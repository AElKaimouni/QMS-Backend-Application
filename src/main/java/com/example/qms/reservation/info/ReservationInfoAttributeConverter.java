package com.example.qms.reservation.info;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import org.json.JSONObject;

public class ReservationInfoAttributeConverter implements AttributeConverter<JSONObject, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(JSONObject attribute) {
        return attribute != null ? attribute.toString() : null;
    }

    @Override
    public JSONObject convertToEntityAttribute(String dbData) {
        return dbData != null ? new JSONObject(dbData) : null;
    }
}
