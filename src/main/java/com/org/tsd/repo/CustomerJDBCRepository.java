package com.org.tsd.repo;

import com.org.tsd.exception.ApplicationException;
import com.org.tsd.models.Address;
import com.org.tsd.models.Customer;
import com.org.tsd.models.UpdateCustomerReq;

public interface CustomerJDBCRepository {
	Customer findCustomer(String principle) throws ApplicationException;
	Customer findCustomerById(Integer cusId) throws ApplicationException;
	Customer updateCust(Integer cusId, UpdateCustomerReq updateReq) throws ApplicationException;
	Customer updateAddress(Integer cusId, Address address) throws ApplicationException;
	Customer createAddress(Integer cusId, Address address) throws ApplicationException;
	void deleteAddress(Integer id) throws ApplicationException;
}