package com.org.tsd.repo;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.org.tsd.exception.ApplicationException;
import com.org.tsd.models.Subscription;

public interface SubscriptionJDBCRepository {

	List<Subscription> list(Integer cusId) throws ApplicationException;

	Subscription getById(Integer id) throws ApplicationException;

	Subscription create(Subscription subscription) throws ApplicationException, SQLException;

	Subscription update(Integer id, Map<String, Object> modReq) throws ApplicationException;

	void delete(Integer id) throws ApplicationException;

}
