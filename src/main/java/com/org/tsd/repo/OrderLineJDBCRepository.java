package com.org.tsd.repo;

import com.org.tsd.exception.ApplicationException;
import com.org.tsd.models.OrderLine;

public interface OrderLineJDBCRepository {

	OrderLine getByOrderLine(Integer cusId, Integer id, Integer lineId) throws ApplicationException;

}
