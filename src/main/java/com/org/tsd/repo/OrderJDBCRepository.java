package com.org.tsd.repo;

import java.util.List;
import java.util.Map;

import com.org.tsd.exception.ApplicationException;
import com.org.tsd.models.Order;
import com.org.tsd.models.OrderLine;

public interface OrderJDBCRepository {

	List<Order> findOrderByOrderId(Integer id) throws ApplicationException;
	
	Order getById(int orderId) throws ApplicationException;

	Order createOrder(Order order) throws ApplicationException;

	Order updateOrder(Integer id, Map<String, Object> subscription)throws ApplicationException;

	void deleteById(Integer id) throws ApplicationException;

	OrderLine getOrderLine(Integer cusId, Integer orderId, Integer lineId) throws ApplicationException;

}
