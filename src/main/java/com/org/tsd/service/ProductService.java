package com.org.tsd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.org.tsd.exception.ApplicationException;
import com.org.tsd.models.ProductInCatalog;
import com.org.tsd.models.ProductListing;
import com.org.tsd.repo.ProductJDBCRepository;

@Service
public class ProductService {
	
	@Autowired 
	private ProductJDBCRepository productJDBCRepo;
	
	public ProductListing getProductListing(Integer cusId) {
		try {
			return productJDBCRepo.getProductListing(cusId);
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ProductInCatalog getProductById(Integer cusId, Integer id) {
		try {
			return productJDBCRepo.getProductById(cusId,id);
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		return null;
	}

}