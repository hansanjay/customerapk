package com.org.tsd.repo.impl;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.org.tsd.exception.ApplicationException;
import com.org.tsd.models.ProductInCatalog;
import com.org.tsd.models.Subscription;
import com.org.tsd.repo.ProductJDBCRepository;
import com.org.tsd.repo.SubscriptionJDBCRepository;
import com.org.tsd.utils.SQLQuery;

@Repository
public class SubscriptionJDBCRepositoryImpl implements SubscriptionJDBCRepository {

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionJDBCRepositoryImpl.class);

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	ProductJDBCRepository productJDBCRepository;

	@Override
	public List<Subscription> list(Integer id) throws ApplicationException {
		try {
			List<Subscription> subscriptions = jdbcTemplate.query(SQLQuery.selVisibleSub,
					new RowMapper<Subscription>() {
						@Override
						public Subscription mapRow(ResultSet rs, int rowNum) throws SQLException {
							Subscription sub = new Subscription();
							sub.setId(rs.getInt("id"));
							sub.setCustomerId(rs.getInt("customer_id"));
							sub.setQuantity(rs.getInt("quantity"));
							sub.setDistributorId(rs.getInt("distributor_id"));
							sub.setProductId(rs.getInt("product_id"));
							sub.setType(rs.getInt("type"));
							sub.setStatus(rs.getInt("status"));
							return sub;
						}
					}, id);
			List<ProductInCatalog> products = productJDBCRepository.getProductListing(id).getProducts();
			for (Subscription sub : subscriptions) {
				for (ProductInCatalog product : products) {
					if (product.getId() == sub.getProductId()) {
						sub.setProduct(product);
						break;
					}
				}
			}
			return subscriptions;
		} catch (Exception ex) {
			throw new ApplicationException(0, "Failed to load subscriptions. " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public Subscription getById(Integer id) throws ApplicationException {
		try {
			Subscription subscription = jdbcTemplate.queryForObject(SQLQuery.selCatId, Subscription.class, id);
			if (subscription != null) {
				List<ProductInCatalog> products = productJDBCRepository.getProductListing(subscription.getCustomerId())
						.getProducts();
				for (ProductInCatalog product : products) {
					if (product.getId().equals(subscription.getProductId())) {
						subscription.setProduct(product);
						break;
					}
				}
			} else {
				throw new ApplicationException(0, "Unable to find subscription with id " + id, HttpStatus.BAD_REQUEST);
			}
			return subscription;

		} catch (DataAccessException ex) {
			throw new ApplicationException(0, "Failed to load subscription. " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public Subscription create(Subscription s) throws ApplicationException, SQLException {
		int id = jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(SQLQuery.creSub, new String[] { "id" });
			ps.setInt(1, s.getCustomerId());
			ps.setInt(2, s.getDistributorId());
			ps.setInt(3, s.getProductId());
			ps.setInt(4, s.getQuantity());
			ps.setInt(5, s.getType());
			ps.setString(6, s.getDayOfWeek());
			ps.setString(7, s.getDayOfMonth());
			ps.setInt(8, s.getStatus());
			ps.setDate(9, new java.sql.Date(s.getStart().getTime()));
			ps.setDate(10, new java.sql.Date(s.getStop().getTime()));
			ps.setBoolean(11, s.isPermanent());
			ps.setBoolean(12, s.isVisible());
			ps.setInt(13, s.getParentId());
			return ps;
		});
		s.setId(id);
		return s;
	}

	@Override
	public Subscription update(Integer id, Map<String, Object> m) throws ApplicationException {
		try {
			Subscription s1 = null;
			Subscription s = getById(id);

			if ((int) m.get("changeType") == 2) {
				s1 = new Subscription(s, m);
				s1 = create(s1);

				m.put("changeType", 2);
				m.put("pause", m.get("start"));
				m.put("resume", m.get("stop"));
				m.put("status", 2);
				m.put("visible", false);
			}

			Map<String, Object> updMap = s.getEditableFields().stream().filter(m::containsKey)
					.collect(Collectors.toMap(k -> k, m::get));

			updMap.forEach((key, value) -> {
				s.setProperties(updMap, false);
				if (value instanceof Date) {
					value = new java.sql.Date(((Date) value).getTime());
				}
			});

			// Ensure 'visible' status is correctly updated
			updMap.put("visible", Integer.parseInt(m.get("changeType").toString()) != 2);

			// Construct the update SQL query dynamically
			StringBuilder updateSql = new StringBuilder("UPDATE SUBSCRIPTION SET ");
			String[] keys = updMap.keySet().toArray(new String[0]);
			String setClause = String.join(", ", Arrays.stream(keys).map(k -> k + " = ?").toArray(String[]::new));
			updateSql.append(setClause).append(" WHERE ID = ?");

			// Add the values to the list, with ID as the final parameter
			List<Object> values = new ArrayList<>(updMap.values());
			values.add(id); // Appending the ID to the end for the WHERE clause

			jdbcTemplate.update(updateSql.toString(), values.toArray());

			return s1 != null ? s1 : s;

		} catch (DataAccessException | SQLException ex) {
			// Log error and re-throw with proper application exception
			logger.error("Failed to update subscription", ex);
			throw new ApplicationException(0, "Failed to update subscription. " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public void delete(Integer id) throws ApplicationException {
		try {
			Subscription s = getById(id);
			if (s == null) {
				throw new ApplicationException(0, "Subscription not found.", HttpStatus.BAD_REQUEST);
			}
			String deleteSql = "DELETE FROM subscription WHERE id = ?";
			jdbcTemplate.update(deleteSql, id);
		} catch (DataAccessException ex) {
			logger.error("Failed to delete subscription", ex);
			throw new ApplicationException(0, "Failed to delete subscription. " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
