package com.example.qms.queue.services;

import com.example.qms.queue.config.QueueConfigField;
import com.example.qms.queue.enums.QueueConfigFieldType;
import com.example.qms.queue.exceptions.UnvalidReservationInfoException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueueFieldsService {
    // Method to validate a JSON object against a list of QueueConfigField rules
    public void validateJson(JSONObject json, List<QueueConfigField> fieldRules) throws UnvalidReservationInfoException {
        for (QueueConfigField field : fieldRules) {
            String fieldName = field.getName();

            // Check if the required field is missing
            if (field.isRequired() && !json.has(fieldName)) {
                System.out.println("Missing required field: " + fieldName);
                throw new UnvalidReservationInfoException();
            }

            // If the field exists, check its type
            if (json.has(fieldName)) {
                Object fieldValue = json.get(fieldName);
                if (!validateType(fieldValue, field.getType())) {
                    System.out.println("Field " + fieldName + " has incorrect type. Expected: " + field.getType());
                    throw new UnvalidReservationInfoException();
                }
            }
        }
    }

    // Helper method to validate if the type of a field matches the expected type
    private boolean validateType(Object value, QueueConfigFieldType expectedType) {
        switch (expectedType) {
            case TEXT:
                return value instanceof String;
            case EMAIL:
                return value instanceof String;
            case BOOLEAN:
                return value instanceof Boolean;
            case NUMBER:
                return value instanceof Integer || value instanceof Double || value instanceof Float;
            case PHONE:
                return value instanceof String;
            case DATE:
                return value instanceof String;
            default:
                return false; // Unsupported type
        }
    }
}
