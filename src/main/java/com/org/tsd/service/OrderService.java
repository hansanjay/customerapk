package com.org.tsd.service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.org.tsd.exception.ApplicationException;
import com.org.tsd.models.Order;
import com.org.tsd.models.OrderLine;
import com.org.tsd.repo.OrderJDBCRepository;
import com.org.tsd.repo.OrderLineJDBCRepository;

@Service
public class OrderService{
	
	private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
	
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    
    @Autowired
    private OrderJDBCRepository orderJDBCRepository;
    
    private OrderLineJDBCRepository orderLineJDBCRepository;

	public Order getById(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	public OrderLine getOrderLine(Integer cusId, Integer id, Integer lineId) {
		try {
			return orderLineJDBCRepository.getByOrderLine(cusId,id,lineId);
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	public List<Order> list(Integer id) {
		try {
			return orderJDBCRepository.findOrderByOrderId(id);
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		return null;
    }

	
	public Order create(Order order) {
		try {
			return orderJDBCRepository.createOrder(order);
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Order update(Integer id, Map<String, Object> subscription) {
		try {
			return orderJDBCRepository.updateOrder(id,subscription);
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void delete(Integer id) {
		try {
			orderJDBCRepository.deleteById(id);
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		
	}

}
