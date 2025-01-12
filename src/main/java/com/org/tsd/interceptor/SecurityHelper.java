package com.org.tsd.interceptor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.tsd.config.AppConfig;
import com.org.tsd.models.Credentials;

import jakarta.annotation.PostConstruct;

@Service
public class SecurityHelper { 

    @Autowired
    private AppConfig config;

    private List<Credentials> credentialStore = new ArrayList<>();

    @PostConstruct
    public void init() {
        List<Map<String, Object>> json = readJsonFileFromClasspath("tokens.json");
        for (Map<String, Object> entry : json) {
            credentialStore.add(new Credentials(entry));
        }
    }

    public Credentials lookupCredentials(String subject) {
        return credentialStore.stream()
                .filter(credential -> credential.getClientId().equals(subject))
                .findFirst()
                .orElse(new Credentials(config.getAddInId()));
    }

    private List<Map<String, Object>> readJsonFileFromClasspath(String filePath) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream resource = this.getClass().getClassLoader().getResourceAsStream(filePath);

            if (resource == null) {
                throw new IllegalArgumentException("File not found: " + filePath);
            }

            return objectMapper.readValue(resource, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to read JSON file from classpath: " + filePath, e);
        }
    }
}
