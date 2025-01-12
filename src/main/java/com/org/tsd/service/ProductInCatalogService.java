package com.org.tsd.service;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.org.tsd.models.ProductInCatalog;
import com.org.tsd.models.ProductListing;

@Service
public class ProductInCatalogService{
	
	private static final Logger logger = LoggerFactory.getLogger(ProductInCatalogService.class);

    @Autowired
    DataSource dataSource;
    
    
    public Integer getCatalogId(int id) {
		
    	return null;
    }
    
    
   public ProductListing getProductListing(Integer id) {
	
	   
	   return null;
 	
    }
   
   public ProductInCatalog getProductById(Integer cusId, Integer id) {

	   return null;
	   
   }

}
