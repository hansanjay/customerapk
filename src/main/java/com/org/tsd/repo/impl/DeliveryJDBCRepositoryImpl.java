package com.org.tsd.repo.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.org.tsd.exception.ApplicationException;
import com.org.tsd.models.Order;
import com.org.tsd.models.OrderLine;
import com.org.tsd.models.Product;
import com.org.tsd.models.ProductInCatalog;
import com.org.tsd.repo.DeliveryJDBCRepository;
import com.org.tsd.service.ProductInCatalogService;
import com.org.tsd.utils.SQLQuery;

@Repository
public class DeliveryJDBCRepositoryImpl implements DeliveryJDBCRepository {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	ProductInCatalogService productInCatalogService;

	public List<Order> list(Integer id) throws ApplicationException {
	    try {
	        List<Map<String, Object>> orderLineRows = jdbcTemplate.queryForList(SQLQuery.selCusDelLines, id);
	        List<OrderLine> orderLines = orderLineRows.stream()
	                .map(OrderLine::new)
	                .collect(Collectors.toList());

	        Map<Integer, List<OrderLine>> linesByOrderId = orderLines.stream()
	                .collect(Collectors.groupingBy(OrderLine::getOrder_id));

	        List<Map<String, Object>> orderRows = jdbcTemplate.queryForList(SQLQuery.selCusDel, id);
	        List<Order> orders = orderRows.stream()
	                .map(Order::new)
	                .collect(Collectors.toList());
	        
	        orders.forEach(order -> {
	            List<OrderLine> lines = linesByOrderId.get(order.getId());
	            if (lines != null) {
	                order.getLines().addAll(lines);
	            }
	        });
	        
	        orders.removeIf(order -> order.getLines().isEmpty());
	        List<ProductInCatalog> products = productInCatalogService.getProductListing(id).getProducts();
	        orders.forEach(order -> {
	            order.getLines().forEach(line -> 
	                line.setProduct(
	                    products.stream()
	                            .filter(product -> product.getId().equals(line.getProduct_id()))
	                            .findFirst()
	                            .orElse(null)
	                )
	            );
	        });
	        
	        return orders;
	    } catch (Exception ex) {
	        throw new ApplicationException(0, "Failed to load orders. " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	
	public Order getById(Integer id) throws ApplicationException {
	    try {
	    	Order order = jdbcTemplate.queryForObject(SQLQuery.selOrd,BeanPropertyRowMapper.newInstance(Order.class), id);

	        if (order == null) {
	            throw new ApplicationException(0, "Unable to find delivery with id " + id, HttpStatus.BAD_REQUEST);
	        }
	        List<OrderLine> orderLines = jdbcTemplate.query(SQLQuery.selCusOrdLines, BeanPropertyRowMapper.newInstance(OrderLine.class),id);
	        order.setLines(orderLines);

	        List<ProductInCatalog> products = productInCatalogService.getProductListing(order.getCustomer_id()).getProducts();
	        for (OrderLine ol : orderLines) {
	            Product product = products.stream()
	                .filter(p -> p.getId().equals(ol.getProduct_id()))
	                .findFirst()
	                .orElse(null);
	            ol.setProduct(product);
	        }
	        return order;
	    } catch (EmptyResultDataAccessException ex) {
	        throw new ApplicationException(0, "Unable to find delivery with id " + id, HttpStatus.BAD_REQUEST);
	    } catch (DataAccessException ex) {
	        throw new ApplicationException(0, "Failed to load order. " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	public OrderLine getOrderLine(Integer cusId, Integer orderId, Integer lineId) throws ApplicationException {
	    try {
	        
	        List<OrderLine> orderLines = jdbcTemplate.query(SQLQuery.selCusOrdLines, BeanPropertyRowMapper.newInstance(OrderLine.class),lineId);
	        if (orderLines.size()== 0) {
	            throw new ApplicationException(0, 
	                "Unable to find Order Line with orderId " + orderId + " and lineId " + lineId, 
	                HttpStatus.BAD_REQUEST);
	        }
	        OrderLine orderLine = orderLines.get(0);
	        List<ProductInCatalog> products = productInCatalogService.getProductListing(cusId).getProducts();

	        Product product = products.stream()
	            .filter(p -> p.getId().equals(orderLine.getProduct_id()))
	            .findFirst()
	            .orElse(null);
	        orderLine.setProduct(product);

	        return orderLine;
	    } catch (EmptyResultDataAccessException ex) {
	        // Handle no results for the given orderId and lineId
	        throw new ApplicationException(0, 
	            "Unable to find Order Line with orderId " + orderId + " and lineId " + lineId, 
	            HttpStatus.BAD_REQUEST);
	    } catch (DataAccessException ex) {
	        // Catch any other database access issues
	        throw new ApplicationException(0, 
	            "Failed to load Order Line. " + ex.getMessage(), 
	            HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
}
