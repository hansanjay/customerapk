package com.org.tsd.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import com.org.tsd.exception.ApplicationException;
import com.org.tsd.models.Address;
import com.org.tsd.models.AuthRequest;
import com.org.tsd.models.Customer;
import com.org.tsd.models.OTPRequest;
import com.org.tsd.models.UpdateCustomerReq;
import com.org.tsd.models.User;
import com.org.tsd.repo.CustomerJDBCRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.SneakyThrows;

@Service
@EnableAsync
public class CustomerRegService { 
	
	private static final Logger logger = LoggerFactory.getLogger(CustomerRegService.class);
	
	@Value("${user.auth.secret.key}")
	private String secretKey;
	
	@Autowired
	private CustomerJDBCRepository customerJDBCRepository;
	
	@Autowired
	private EmailService emailService;

	@SneakyThrows
	public void generateOTP(OTPRequest otpRequest) {
		User user = customerJDBCRepository.findCustomer(otpRequest.getPrincipal());
		if(null != user) {
			String otp = String.format("%04d", new Random().nextInt(10000));
			customerJDBCRepository.updateCust(user.getId().toString(),otp);
			sendOTP(user,otp);
		    System.out.println("OTP sent successfully to "+otpRequest.getPrincipal());
		    logger.info("Received request to generate OTP");
		}
	}
	
	@Async
	void sendOTP(User user, String otp) {
        emailService.sendSimpleEmail(user.getEmail(), "Your OTP to login into TSD application is "+otp,
                "Hi,"
                + "Your OTP to login into TSD application is "+otp+"."
                + "This OTP is valid for 5 minutes. Use this to complete logging into the application."
                + "Regards,"
                + "Smart Delivery Team");
    }
	
	@SneakyThrows
	public String generateAuthToken(AuthRequest request) throws ApplicationException {
		User user = customerJDBCRepository.findCustomer(request.getPrincipal());
		if (null == user) {
			throw new ApplicationException(0, "BAD REQUEST", HttpStatus.BAD_REQUEST);
		} else if (Integer.parseInt(user.getOtp().trim()) == Integer.parseInt(request.getOtp().trim())) {
			Map<String, Object> claims = new HashMap<>();
			claims.put("sub", request.getPrincipal());
			claims.put("iat", System.currentTimeMillis());
			claims.put("expiry", LocalDateTime.now().plusHours(2).toString());
			claims.put("deviceId", request.getDeviceId());
			claims.put("user", user);
			SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
			return Jwts.builder().setClaims(claims).signWith(key).compact();
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
			customerJDBCRepository.createAddress(cusId, address);
			return getById(cusId);
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void deleteAddress(Integer id) {
		try {
			customerJDBCRepository.deleteAddress(id);
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
	}
	
}