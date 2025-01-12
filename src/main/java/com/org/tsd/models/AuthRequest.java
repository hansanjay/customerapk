package com.org.tsd.models;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthRequest {

    @NotBlank(message = "Field cannot be left blank")
    String deviceId;
    
    @NotBlank(message = "Field cannot be left blank")
    String principal;
    
    @NotBlank(message = "Field cannot be left blank")
    String otp;
    
    String rid;

    public boolean verify(OTPDetails otpDetails) {
        return otpDetails.otp == otp &&
               otpDetails.request.principal == principal &&
               otpDetails.request.deviceId == deviceId &&
               otpDetails.request.rid == rid &&
               otpDetails.request.isValid();
    }
}
