package com.org.tsd.models;

import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ProductListing {
	int id;
    List<Brand> brands;
    List<Category> categories;
    List<ProductInCatalog> products;
    
    public ProductListing(int catalogId, 
    		List<Brand> brands, 
    		List<Category> categories,
			List<ProductInCatalog> products) {
    	this.id=catalogId;
    	this.brands=brands;
    	this.categories=categories;
    	this.products=products;
	}
}