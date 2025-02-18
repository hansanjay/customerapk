package com.org.tsd.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.org.tsd.exception.ApplicationException;
import com.org.tsd.models.Subscription;
import com.org.tsd.repo.SubscriptionJDBCRepository;

@Service
public class SubscriptionService {

	@Autowired
	SubscriptionJDBCRepository subscriptionJDBCRepository;

	public List<Subscription> list(Integer cusId) {
		try {
			return subscriptionJDBCRepository.list(cusId);
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Subscription getById(Integer id) {
		try {
			return subscriptionJDBCRepository.getById(id);
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Subscription create(Subscription subscription) {
		try {
			return subscriptionJDBCRepository.create(subscription);
		} catch (ApplicationException | SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void delete(Integer id) {
		try {
			subscriptionJDBCRepository.delete(id);
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
	}

	public Subscription pause(Integer id, Map<String, Object> of) {
		try {
			return subscriptionJDBCRepository.pause(id, of);
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Subscription resume(Integer id, Map<String, Object> of) {
		try {
			return subscriptionJDBCRepository.pause(id, of);
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void removeChildren(Integer id) {
		subscriptionJDBCRepository.removeChildren(id);
	}
}
