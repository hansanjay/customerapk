package com.org.tsd.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.org.tsd.exception.ApplicationException;
import com.org.tsd.models.Order;
import com.org.tsd.models.OrderLine;
import com.org.tsd.repo.DeliveryJDBCRepository;

@Service
public class DeliveryService {

	@Autowired
	DeliveryJDBCRepository deliveryJDBCRepository;

	public List<Order> list(Integer cusId) {
		try {
			deliveryJDBCRepository.list(cusId);
		} catch (ApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Order getById(Integer id) {
		try {
			deliveryJDBCRepository.getById(id);
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public OrderLine getOrderLine(Integer cusId, Integer id, Integer lineId) {
		try {
			deliveryJDBCRepository.getOrderLine(cusId, id, lineId);
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Order create(Order order) {
		// TODO Auto-generated method stub
		return null;
	}

	public Order update(Integer id, Map<String, Object> updates) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
