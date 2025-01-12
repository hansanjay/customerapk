package com.org.tsd.repo;

import com.org.tsd.exception.ApplicationException;
import com.org.tsd.models.ProductInCatalog;
import com.org.tsd.models.ProductListing;

public interface ProductJDBCRepository {

	ProductListing getProductListing(Integer cusId) throws ApplicationException;

	ProductInCatalog getProductById(Integer cusId, Integer id) throws ApplicationException;

}
