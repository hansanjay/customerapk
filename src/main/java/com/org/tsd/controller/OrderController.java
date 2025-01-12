package com.org.tsd.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.org.tsd.config.AppConfig;
import com.org.tsd.models.Order;
import com.org.tsd.models.OrderLine;
import com.org.tsd.service.OrderService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600L)
@RequestMapping("order")
public class OrderController {
	
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private AppConfig config;

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<?> getOrders(HttpServletRequest request,
                                        @RequestParam(defaultValue = "25") Integer limit,
                                        @RequestParam(defaultValue = "0") Integer offset) {
       
    	String id =  request.getAttribute("_X_AUTH_ID").toString();
    	Integer cusId = Integer.valueOf(id);
        List<Order> total = orderService.list(cusId);
        List<Order> list = total.subList(offset, Math.min(offset + limit, total.size()));
        logger.info("Found {} orders", total.size());
        return ResponseEntity.ok(Map.of(
                "count", list.size(),
                "total", total.size(),
                "hasMore", total.size() > list.size(),
                "limit", limit,
                "offset", offset,
                "items", list
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getById(@PathVariable Integer id) {
        return new ResponseEntity<>(orderService.getById(id),HttpStatus.OK);
    }


	@GetMapping("/{id}/line/{lineId}")
    public ResponseEntity<OrderLine> getOrderLine(HttpServletRequest request,
                                                  @PathVariable Integer id,
                                                  @PathVariable Integer lineId) {
		String requestId =  request.getAttribute("_X_AUTH_ID").toString();
    	Integer cusId = Integer.valueOf(requestId);
        return new ResponseEntity<>(orderService.getOrderLine(cusId, id, lineId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Order> create(HttpServletRequest request, @RequestBody Order order) {
    	String id =  request.getAttribute("_X_AUTH_ID").toString();
    	Integer cusId = Integer.valueOf(id);
    	order.setCustomerId(cusId); 
        return new ResponseEntity<>(orderService.create(order),HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Order> update(@PathVariable Integer id, @RequestBody Map<String, Object> subscription) {
        return new ResponseEntity<>(orderService.update(id, subscription),HttpStatus.OK);
    }

    // Uncomment if you want to enable delete functionality
    /*
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        helper.delete(id);
        return ResponseEntity.noContent().build();
    }
    */

}
