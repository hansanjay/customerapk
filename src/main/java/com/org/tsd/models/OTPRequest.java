package com.org.tsd.models;

import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OTPRequest {
	
	@NotBlank(message = "Field cannot be left blank")
    String deviceId;
	
	@NotBlank(message = "Field cannot be left blank")
	String principal;
    
	String rid;

    private final long createdAt = new Date().getTime();
    
    public boolean isValid() {
        long currentTime = System.currentTimeMillis();
        return (createdAt + 180000) > currentTime;
    }
}
