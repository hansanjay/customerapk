package com.org.tsd.repo;

import java.util.List;

import com.org.tsd.exception.ApplicationException;
import com.org.tsd.models.Order;
import com.org.tsd.models.OrderLine;

public interface DeliveryJDBCRepository {
	public List<Order> list(Integer id) throws ApplicationException;
	public Order getById(Integer id) throws ApplicationException;
	public OrderLine getOrderLine(Integer cusId, Integer orderId, Integer lineId) throws ApplicationException;
	
}