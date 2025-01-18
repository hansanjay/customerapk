package com.org.tsd.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.org.tsd.exception.ApplicationException;
import com.org.tsd.models.Subscription;
import com.org.tsd.service.SubscriptionService;

import jakarta.servlet.http.HttpServletRequest;


@RestController
@CrossOrigin(origins = "*", maxAge = 3600L)
@RequestMapping("/subscription")
public class SubscriptionController {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionController.class);

    @Autowired
    SubscriptionService subscriptionService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> get(HttpServletRequest request,@RequestParam(defaultValue = "25") Integer limit,@RequestParam(defaultValue = "0") Integer offset) {
		Integer cusId = (Integer) request.getAttribute("_X_AUTH_ID");
		List<Subscription> total;
		List<Subscription> list;
		total = subscriptionService.list(cusId);
		list = total.stream().skip(offset).limit(limit).collect(Collectors.toList());
		logger.info("Found {} subscriptions", total.size());
		return new ResponseEntity<>(Map.of("count", list.size(), "total", total.size(), "hasMore",
				total.size() > (list.size() + offset), "limit", limit, "offset", offset, "items", list), HttpStatus.OK);
	}

    @GetMapping("/{id}")
    public ResponseEntity<Subscription> getById(@PathVariable Integer id){
        return new ResponseEntity<>(subscriptionService.getById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Subscription> create(HttpServletRequest request, @RequestBody Subscription subscription){
        Integer cusId = (Integer) request.getAttribute("_X_AUTH_ID");
        subscription.setCustomer_id(cusId);
        subscription.setParent_id(null);
        subscription.setPermanent(true);
        subscription.setVisible(true);
        subscription = subscriptionService.create(subscription);
        return new ResponseEntity<>(subscription, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Subscription> update(@PathVariable Integer id, @RequestBody Map<String, Object> modReq){
        return new ResponseEntity<>(subscriptionService.update(id, modReq), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) throws ApplicationException {
    	subscriptionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/pause")
    public ResponseEntity<Subscription> pause(@PathVariable Integer id, @RequestBody Subscription subscription) throws ApplicationException{
        if (subscription.getPause() == null || subscription.getResume() == null
                || subscription.getResume().before(subscription.getPause())
                || subscription.getResume().before(new Date())) {
            throw new ApplicationException(0, "Invalid pause or resume dates.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(
        		subscriptionService.update(id, Map.of(
                        "status", 2,
                        "pause", subscription.getPause(),
                        "resume", subscription.getResume(),
                        "changeType","1"
                )),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}/resume")
    public ResponseEntity<Subscription> resume(@PathVariable Integer id){
    	subscriptionService.delete(id);
        return new ResponseEntity<>(
        		subscriptionService.update(id, Map.of(
                        "status", 1,
                        "pause", null,
                        "resume", null,
                        "visible", true,
                        "changeType","0"
                )),
                HttpStatus.OK
        );
    }
}
