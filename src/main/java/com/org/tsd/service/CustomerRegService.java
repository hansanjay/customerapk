package com.org.tsd.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import com.org.tsd.exception.ApplicationException;
import com.org.tsd.models.Address;
import com.org.tsd.models.AuthRequest;
import com.org.tsd.models.Customer;
import com.org.tsd.models.OTPDetails;
import com.org.tsd.models.OTPRequest;
import com.org.tsd.models.UpdateCustomerReq;
import com.org.tsd.repo.CustomerJDBCRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Path.ReturnValueNode;
import lombok.SneakyThrows;

@Service
@EnableAsync
public class CustomerRegService {
	
	private static final Logger logger = LoggerFactory.getLogger(CustomerRegService.class);
	
	private static ConcurrentHashMap<String,OTPDetails> otpStorage = new ConcurrentHashMap<>();
	
	@Autowired
	private CustomerJDBCRepository customerJDBCRepository;
	
	@Autowired
	private EmailService emailService;

	@SneakyThrows
	public void generateOTP(OTPRequest otpRequest) {
		Customer customer = customerJDBCRepository.findCustomer(otpRequest.getPrincipal());
		if(null != customer) {
			String otp = String.format("%04d", new Random().nextInt(10000));
			sendOTP(customer,otp);
		    otpStorage.put(otpRequest.getPrincipal(), new OTPDetails(otp,otpRequest));
		    System.out.println("OTP sent successfully to "+otpRequest.getPrincipal());
		    logger.info("Received request to generate OTP");
		}
	    
	}
	
	@Async
	void sendOTP(Customer user, String otp) {
        emailService.sendSimpleEmail(user.getEmail(), "Your OTP to login into TSD application is "+otp,
                "Hi,"
                + "Your OTP to login into TSD application is "+otp+"."
                + "This OTP is valid for 5 minutes. Use this to complete logging into the application."
                + "Regards,"
                + "Smart Delivery Team");
    }
	
	public String generateAuthToken(AuthRequest request) throws ApplicationException {
        // Retrieve OTP details
        OTPDetails otpDetails = otpStorage.get(request.getPrincipal());

        // Validate the OTP details and request
        if (otpDetails != null && request.getOtp().equals(otpDetails.getOtp())) {
            otpStorage.remove(request.getPrincipal());

            // Fetch the authenticated user details
            Customer customer = customerJDBCRepository.findCustomer(request.getPrincipal());

            // Prepare JWT claims
            Map<String, Object> claims = new HashMap<>();
            claims.put("sub", request.getPrincipal());
            claims.put("iat", System.currentTimeMillis());
            claims.put("expiry", LocalDateTime.now().plusHours(2).toString());
            claims.put("deviceId", request.getDeviceId());
            claims.put("user", customer);

            // Fetch the secret key for signing the JWT
            String secret = "cBZchf6NNTbG55NqexpW4AZ3vn41Nj42He";
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

            // Generate and return the signed JWT
            return Jwts.builder()
                       .setClaims(claims)
                       .signWith(key)
                       .compact();
        } else {
            throw new ApplicationException(0, "BAD REQUEST", HttpStatus.BAD_REQUEST);
        }
    }

	public Customer getById(Integer cusId) {
		Customer customer;
		try {
			customer = customerJDBCRepository.findCustomerById(cusId);
			return customer;
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Customer update(Integer cusId, UpdateCustomerReq updateReq) {
		try {
			return customerJDBCRepository.updateCust(cusId,updateReq);
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Customer updateAddress(Integer cusId, Address address) {
		try {
			return customerJDBCRepository.updateAddress(cusId,address);
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Address getAddressById(Integer cusId, Integer id) {
		Customer customer = getById(cusId);
	    if (customer == null) {
	        throw new IllegalArgumentException("Customer with ID " + cusId + " not found.");
	    }
	    List<Address> addList = customer.getAddressList();
	    if (addList == null || addList.isEmpty()) {
	        throw new IllegalStateException("No addresses found for customer with ID " + cusId);
	    }
	    Optional<Address> address = addList.stream()
	            .filter(a -> a.getId() != 0 && a.getId() == id)
	            .findFirst();

	    return address.orElseThrow(() -> 
        			new NoSuchElementException("Address with ID " + id + " not found for customer " + cusId));
	}

	public Customer createAddress(Integer cusId, Address address) {
		try {
			customerJDBCRepository.createAddress(cusId,address);
			return getById(cusId);
		} catch (ApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void deleteAddress(Integer id) {
		
	}
	
}