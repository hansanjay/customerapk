package com.org.tsd.repo.impl;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.org.tsd.exception.ApplicationException;
import com.org.tsd.models.Brand;
import com.org.tsd.models.Category;
import com.org.tsd.models.ProductInCatalog;
import com.org.tsd.models.ProductListing;
import com.org.tsd.repo.ProductJDBCRepository;
import com.org.tsd.utils.SQLQuery;

@Repository
public class ProductJDBCRepoImpl implements ProductJDBCRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(ProductJDBCRepoImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public Integer getCatalogId(int customerId) throws ApplicationException {
        try {
            return jdbcTemplate.queryForObject(SQLQuery.selCatId, Integer.class, customerId);
        } catch (Exception ex) {
            if (ex instanceof EmptyResultDataAccessException) {
                throw new ApplicationException(0, "Unable to find product catalog ID for customer", HttpStatus.BAD_REQUEST);
            } else if (ex instanceof SQLException) {
                throw new ApplicationException(0, "Database failure: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                throw new ApplicationException(0, "Unexpected error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

	@Override
	public ProductListing getProductListing(int cusId) throws ApplicationException {
		try {
            int catalogId = getCatalogId(cusId);
            List<ProductInCatalog> products = jdbcTemplate.query(SQLQuery.selPrd,BeanPropertyRowMapper.newInstance(ProductInCatalog.class), catalogId);
            List<Brand> brands = jdbcTemplate.query(SQLQuery.selBrands,BeanPropertyRowMapper.newInstance(Brand.class), catalogId);
            List<Category> categories = jdbcTemplate.query(SQLQuery.selCat,BeanPropertyRowMapper.newInstance(Category.class), catalogId);
            return new ProductListing(
                    catalogId,
                    brands,
                    categories,
                    products
            );
        } catch (ApplicationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	

	@Override
	public ProductInCatalog getProductById(Integer customerId, Integer productId) throws ApplicationException {
	    try {
	        List<ProductInCatalog> products = getProductListing(customerId).getProducts();
	        return products.stream()
	                .filter(product -> product.getId() == productId)
	                .findFirst()
	                .orElseThrow(() -> new ApplicationException(
	                        0, "Product not found for ID: " + productId, HttpStatus.NOT_FOUND));
	    } catch (Exception ex) {
	        throw new ApplicationException(
	                0, "Failed to load product. " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

}