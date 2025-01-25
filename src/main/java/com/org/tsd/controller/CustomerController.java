package com.org.tsd.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.tsd.models.Address;
import com.org.tsd.models.Customer;
import com.org.tsd.models.UpdateCustomerReq;
import com.org.tsd.service.CustomerRegService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600L)
@RequestMapping("/customer")
class CustomerController {

    private static Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
	CustomerRegService customerRegService;

    @GetMapping("/profile")
    public ResponseEntity<Customer> profile(HttpServletRequest request) {
    	String id = request.getAttribute("_X_AUTH_ID").toString();
        Integer cusId = Integer.parseInt(id);
        return new ResponseEntity<>(customerRegService.getById(cusId), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getById(@PathVariable Integer id) {
        return new ResponseEntity<>(customerRegService.getById(id), HttpStatus.OK);
    }

    @PatchMapping("/profile")
    public ResponseEntity<Customer> updateProfile(HttpServletRequest request, @RequestBody UpdateCustomerReq updateReq) {
    	String id = request.getAttribute("_X_AUTH_ID").toString();
        Integer cusId = Integer.parseInt(id);
        Customer customer = customerRegService.update(cusId, updateReq);
        return new ResponseEntity<Customer>(customer,HttpStatus.OK);
    }

    @PatchMapping("/address")
    public ResponseEntity<Customer> updateAddress(HttpServletRequest request, @RequestBody Address address) {
    	String id = request.getAttribute("_X_AUTH_ID").toString();
        Integer cusId = Integer.parseInt(id);
        Customer customer = customerRegService.updateAddress(cusId, address);
        return new ResponseEntity<>(customer, HttpStatus.OK);
    }

    @GetMapping("/address/{id}")
    public ResponseEntity<Address> getAddress(HttpServletRequest request, @PathVariable Integer id) {
        Integer cusId = Integer.parseInt(request.getAttribute("_X_AUTH_ID").toString());
        return new ResponseEntity<>(customerRegService.getAddressById(cusId, id), HttpStatus.OK);
    }

    @PostMapping("/address")
    public ResponseEntity<Customer> addAddress(HttpServletRequest request, @RequestBody Address address) {
    	String id = request.getAttribute("_X_AUTH_ID").toString();
        Integer cusId = Integer.parseInt(id);
        return new ResponseEntity<>(customerRegService.createAddress(cusId, address), HttpStatus.OK);
    }

    @DeleteMapping("/address/{id}")
    public ResponseEntity<Customer> deleteAddress(HttpServletRequest request, @PathVariable Integer id) {
        customerRegService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}