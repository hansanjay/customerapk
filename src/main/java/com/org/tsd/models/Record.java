package com.org.tsd.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Record {
    private static final Logger logger = LoggerFactory.getLogger(Record.class);
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    // Placeholder for 'getEditableFields' method - overridden in subclasses.
    public List<?> getEditableFields() {
        return Collections.emptyList(); // Default implementation: no editable fields.
    }

    // Method to parse or convert to Date.
    public Date toDate(Object date) {
        if (date != null) {
            try {
                if (date instanceof Date) {
                    return (Date) date;
                } else if (date instanceof String) {
                    return sdf.parse((String) date);
                }
            } catch (ParseException e) {
                logger.error("Failed to parse date: {}", date, e);
            }
        }
        return null;
    }

    // Method to set a Date to midnight (00:00:00.000).
    public static Date setToMidnight(Date date) {
        if (date != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTime();
        }
        return null;
    }

    // Method to set properties using a Map.
    public void setProperties(Map<String, Object> map, boolean ignore) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            try {
                // Assuming a setter method for the property exists:
                // For a real-world scenario, you may use reflection to set properties dynamically.
                // Simplifying this part for clarity.
                // Example: Using "key" dynamically to call the respective setter method.
            } catch (Exception e) {
                if (ignore) {
                    logger.warn("Ignoring missing property: {}", key);
                } else {
                    throw new RuntimeException("Failed to set property: " + key, e);
                }
            }
        }
    }
}
