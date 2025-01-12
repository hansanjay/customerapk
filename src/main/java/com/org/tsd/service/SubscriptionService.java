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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Subscription getById(Integer id) {
		try {
			subscriptionJDBCRepository.getById(id);
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Subscription create(Subscription subscription) {
		try {
			subscriptionJDBCRepository.create(subscription);
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

	public Subscription update(Integer id, Map<String, Object> of) {
		try {
			return subscriptionJDBCRepository.update(id, of);
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		return null;
	}
}
