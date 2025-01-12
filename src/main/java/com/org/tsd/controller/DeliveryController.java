package com.org.tsd.controller;

import java.util.HashMap;
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

import com.org.tsd.models.Order;
import com.org.tsd.models.OrderLine;
import com.org.tsd.service.DeliveryService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("delivery")
public class DeliveryController {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryController.class);

    @Autowired
    private DeliveryService deliveryService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> get(HttpServletRequest request, 
                                                   @RequestParam(defaultValue = "25") Integer limit, 
                                                   @RequestParam(defaultValue = "0") Integer offset) {
        Integer cusId = (Integer) request.getAttribute("_X_AUTH_ID");

        List<Order> total = deliveryService.list(cusId);
        List<Order> list = total.subList(offset, Math.min(offset + limit, total.size()));

        logger.info("Found {} orders", total.size());
        Map<String, Object> response = new HashMap<>();
        response.put("count", list.size());
        response.put("total", total.size());
        response.put("hasMore", total.size() > (offset + limit));
        response.put("limit", limit);
        response.put("offset", offset);
        response.put("items", list);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getById(@PathVariable Integer id) {
        Order order = deliveryService.getById(id);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @GetMapping("/{id}/line/{lineId}")
    public ResponseEntity<OrderLine> getOrderLine(HttpServletRequest request, 
                                                  @PathVariable Integer id, 
                                                  @PathVariable Integer lineId) {
        Integer cusId = (Integer) request.getAttribute("_X_AUTH_ID");
        OrderLine orderLine = deliveryService.getOrderLine(cusId, id, lineId);
        return new ResponseEntity<>(orderLine, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Order> create(HttpServletRequest request, @RequestBody Order order) {
        Integer cusId = (Integer) request.getAttribute("_X_AUTH_ID");
        order.setCustomerId(cusId);
        Order createdOrder = deliveryService.create(order);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Order> update(@PathVariable Integer id, @RequestBody Map<String, Object> updates) {
        Order updatedOrder = deliveryService.update(id, updates);
        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
    }

// Uncomment this method if delete functionality is needed
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable Integer id) {
//        helper.delete(id);
//        return ResponseEntity.noContent().build();
//    }
}
