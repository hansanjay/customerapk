package com.org.tsd.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    private boolean result;
    private String id;
    private String message;
    
    public AuthResponse(boolean result, String message) {
        this.result = result;
        this.message = message;
    }

    public AuthResponse(boolean result, String message, String id) {
        this.result = result;
        this.message = message;
        this.id = id;
    }
}