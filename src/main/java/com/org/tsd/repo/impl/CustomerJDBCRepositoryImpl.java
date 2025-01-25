package com.org.tsd.repo.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.org.tsd.exception.ApplicationException;
import com.org.tsd.models.Address;
import com.org.tsd.models.Customer;
import com.org.tsd.models.UpdateCustomerReq;
import com.org.tsd.models.User;
import com.org.tsd.repo.CustomerJDBCRepository;
import com.org.tsd.utils.SQLQuery;

@Repository
public class CustomerJDBCRepositoryImpl implements CustomerJDBCRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(CustomerJDBCRepositoryImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public User findCustomer(String principle) throws ApplicationException {
		try {
			User user = jdbcTemplate.queryForObject(SQLQuery.selAppUserWithMobile,BeanPropertyRowMapper.newInstance(User.class), principle);
			if (null == user) 
				throw new ApplicationException(0, "Invalid application user", HttpStatus.BAD_REQUEST);
			return user;
		} catch (Exception e) {
			logger.error("Database failure", e);
            throw new ApplicationException(0, "Failed to find application user. $ex.message",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public Customer findCustomerById(Integer cusId) throws ApplicationException {
		try {
			Customer customer = jdbcTemplate.queryForObject(SQLQuery.selAppUserWithId,BeanPropertyRowMapper.newInstance(Customer.class), cusId);
			if (null == customer) {
				throw new ApplicationException(0, "Invalid application user", HttpStatus.BAD_REQUEST);
			}else {
				List<Address> address = jdbcTemplate.query(SQLQuery.selCusAdd, BeanPropertyRowMapper.newInstance(Address.class), cusId);
				if(address.size() > 0) {
					customer.setAddressList(address);
				}
			}
			return customer;
		} catch (Exception e) {
			logger.error("Database failure", e);
            throw new ApplicationException(0, "Failed to find application user. $ex.message",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public Customer updateCust(Integer cusId, UpdateCustomerReq updateReq) throws ApplicationException {
		try {
			Customer customer = findCustomerById(cusId);
			if(null != customer) {
				int var = jdbcTemplate.update(SQLQuery.updCustProfile,updateReq.getEmail(),updateReq.getFirst_name(),updateReq.getLast_name(),cusId);
				if (0 == var) 
					throw new ApplicationException(0, "Invalid application user", HttpStatus.BAD_REQUEST);
			}
			return findCustomerById(cusId);
		} catch (Exception e) {
			logger.error("Database failure", e);
            throw new ApplicationException(0, "Failed to find application user. $ex.message",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public Customer updateAddress(Integer cusId, Address address) throws ApplicationException {
	 try {
		 jdbcTemplate.update(SQLQuery.updCustAddress,address.getShort_name(), address.getLine1(), address.getLine2(), address.getLine3(),address.getCity(), address.getState_name(), address.getCountry(), address.getPin_code(),address.getGeo_tag(), address.is_default(), cusId, address.getId());
		 return findCustomerById(cusId);
	 } catch (Exception ex) {
            logger.error("Failed to update Order. " +ex.getMessage());
            throw new ApplicationException(0, "Failed to update order. $ex.message", HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}

	@Override
	public Customer createAddress(Integer cusId, Address address) throws ApplicationException {
		try {
			jdbcTemplate.update(SQLQuery.creCusAdd,cusId, address.getShort_name(), address.getLine1(), address.getLine2(), address.getLine3(),
                    address.getCity(), address.getState_name(), address.getCountry(), address.getPin_code(),address.getGeo_tag(), address.is_default());
			return findCustomerById(cusId);
		} catch (Exception ex) {
			logger.error("Failed to update Order. " + ex.getMessage());
			throw new ApplicationException(0, "Failed to update order. $ex.message", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public void deleteAddress(Integer id) throws ApplicationException {
		try {
	        String sql = "DELETE FROM address WHERE id = ?";
	        jdbcTemplate.update(sql, id);
	    } catch (Exception ex) {
	        logger.error("Failed to delete address.", ex);
	        throw new ApplicationException(0, "Failed to delete address. " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    }		
	}

	@Override
	public int updateCust(String userId, String otp) {
		return jdbcTemplate.update(SQLQuery.updateAppUserWithMobile,otp,Long.parseLong(userId));
	}
}