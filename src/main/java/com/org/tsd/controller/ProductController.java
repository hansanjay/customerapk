package com.org.tsd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.tsd.models.ProductInCatalog;
import com.org.tsd.models.ProductListing;
import com.org.tsd.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@RestController
@CrossOrigin(origins = "*", maxAge = 3600L)
@RequestMapping("/product")
public class ProductController {
	
	@Autowired
	ProductService productService;
	
	@GetMapping(path = "/list",produces=MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Fetch Product List", description = "API to fetch all the Product available for subscription")
    ResponseEntity<ProductListing> loadCatalog(HttpServletRequest request, HttpServletResponse response) {
        Integer cusId = Integer.parseInt(request.getAttribute("_X_AUTH_ID").toString());
        ProductListing listing = productService.getProductListing(cusId);
        return new ResponseEntity<>(listing, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    ResponseEntity<ProductInCatalog> getById(HttpServletRequest request, @PathVariable Integer id) {
        Integer cusId = Integer.parseInt(request.getAttribute("_X_AUTH_ID").toString());
        ProductInCatalog product = productService.getProductById(cusId, id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }
	
}
