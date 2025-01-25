package com.org.tsd.repo.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.org.tsd.exception.ApplicationException;
import com.org.tsd.models.Order;
import com.org.tsd.models.OrderLine;
import com.org.tsd.models.Product;
import com.org.tsd.models.ProductInCatalog;
import com.org.tsd.models.Record;
import com.org.tsd.models.Subscription;
import com.org.tsd.repo.OrderJDBCRepository;
import com.org.tsd.repo.ProductJDBCRepository;
import com.org.tsd.service.DBHelper;
import com.org.tsd.utils.SQLQuery;

import lombok.SneakyThrows;

@Repository
public class OrderJDBCRepositoryImpl implements OrderJDBCRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(OrderJDBCRepositoryImpl.class);

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	ProductJDBCRepository productJDBCRepository;
	
	@Override
	@SneakyThrows
	public List<Order> findOrderByOrderId(Integer custId) throws ApplicationException {

		try {
			List<Subscription> subscriptions = jdbcTemplate.query(SQLQuery.selActSub,
					BeanPropertyRowMapper.newInstance(Subscription.class), custId);

			List<OrderLine> orderLines = jdbcTemplate.query(SQLQuery.selCusOrdLines,
					BeanPropertyRowMapper.newInstance(OrderLine.class), custId);

			Map<Integer, List<OrderLine>> lines = new HashMap<>();
			for (OrderLine line : orderLines) {
				lines.computeIfAbsent(line.getOrder_id(), k -> new ArrayList<>()).add(line);
			}

			List<Order> orders = jdbcTemplate.query(SQLQuery.selCusOrd, BeanPropertyRowMapper.newInstance(Order.class),
					custId);

			Date currentDate = Record.setToMidnight(new Date());

			if (!subscriptions.isEmpty()) {
				Subscription s = subscriptions.get(0);
				if (subscriptions.stream().anyMatch(sub -> sub.getType() == 1)) {
					for (int i = 1; i <= 30; i++) {
						int orderId = -(i + 1);
						Date orderDate = addDay(currentDate, i);
						Order order = orders.stream().filter(o -> isEqual(o.getOrderDate(), orderDate)).findFirst()
								.orElseGet(() -> new Order(orderId, s.getCustomer_id(), s.getDistributor_id(), orderId,
										orderDate, "U", null, null));
						orders.add(order);
					}
				} else {
					List<String> daysOfWeek = new ArrayList<>();
					List<String> daysOfMonth = new ArrayList<>();

					for (Subscription sub : subscriptions) {
						if (sub.getType() == 2) {
							daysOfWeek.addAll(Arrays.asList(sub.getDay_of_week().split(",")));
						} else if (sub.getType() == 3) {
							daysOfMonth.addAll(Arrays.asList(sub.getDay_of_month().split(",")));
						}
					}

					for (int i = 1; i <= 30; i++) {
						Date orderDate = addDay(currentDate, i);
						String dayOfWeek = new SimpleDateFormat("u").format(orderDate);
						String dayOfMon = new SimpleDateFormat("d").format(orderDate);
						if (daysOfWeek.contains(dayOfWeek) || daysOfMonth.contains(dayOfMon)) {
							if (orders.stream().noneMatch(o -> isEqual(o.getOrderDate(), orderDate))) {
								orders.add(new Order(-(i + 1), s.getCustomer_id(), s.getDistributor_id(), i, orderDate,
										"U", null, null));
							}
						}
					}
				}
			}

			for (Order o : orders) {
				if (lines.containsKey(o.getId())) {
					o.getLines().addAll(lines.get(o.getId()));
				}

				String dayOfWeek = new SimpleDateFormat("u").format(o.getOrderDate());
				Pattern dayOfWeekPattern = Pattern
						.compile("^" + dayOfWeek + ",|," + dayOfWeek + "$|," + dayOfWeek + ",");
				String dayOfMon = new SimpleDateFormat("d").format(o.getOrderDate());
				Pattern dayOfMonPattern = Pattern.compile("^" + dayOfMon + ",|," + dayOfMon + "$|," + dayOfMon + ",");

				for (Subscription s : subscriptions) {
					if (s.isActiveOn(o.getOrderDate())) {
						if (s.getType() == 1 || (s.getType() == 2 && dayOfWeekPattern.matcher(s.getDay_of_week()).find())
								|| (s.getType() == 3 && dayOfMonPattern.matcher(s.getDay_of_month()).find())) {
							if (null != o.getSubscriptions() && o.getSubscriptions().size() > 0) {
								o.getSubscriptions().add(s);
								if (null != o.getLines() && o.getLines().size() > 0) {
									o.getLines().add(new OrderLine(-(o.getLines().size() + 1), s.getProduct_id(),
											s.getQuantity(), o.getId(), s.getId(), null));
								}
							}
						}
					}
				}
			}
			//orders.removeIf(o -> o.getLines().isEmpty());

			List<ProductInCatalog> products = productJDBCRepository.getProductListing(custId).getProducts();
			for (Order o : orders) {
				if (null != o.getLines()) {
					for (OrderLine ol : o.getLines()) {
						ol.setProduct(products.stream().filter(p -> p.getId() == ol.getProduct_id()).findFirst().orElse(null));
					}
				}
			}
			return orders;
		} catch (Exception ex) {
			throw new ApplicationException(0, "Failed to load order. " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}


	@Override
	@SneakyThrows
	public Order createOrder(Order order) throws ApplicationException, DataAccessException {
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
            int orderId = jdbcTemplate.update(SQLQuery.creOrder, order.getCustomer_id(), DBHelper.toSQLDate(order.getOrderDate()),keyHolder);
            
            if(orderId == 1) {
            	order.setId(keyHolder.getKey().intValue());
            	for (OrderLine orderLine : order.getLines()) {
                    jdbcTemplate.update(SQLQuery.creOrdLine, orderId, orderLine.getProduct_id(), orderLine.getQuantity());
                }
            }
            return getById(orderId);
        } catch (DataAccessException ex) {
            logger.error("Failed to create order.", ex);
            throw new ApplicationException(0, "Failed to create order. " + ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}

	@Override
	@SneakyThrows
	public Order getById(int orderId) throws ApplicationException {
		try {
			
            Order order = jdbcTemplate.queryForObject(SQLQuery.selOrd,BeanPropertyRowMapper.newInstance(Order.class),orderId);
            
            if (order == null) {
                throw new ApplicationException(0, "Unable to find order with id " + orderId, HttpStatus.BAD_REQUEST);
            }

            List<OrderLine> orderLines = jdbcTemplate.query(SQLQuery.selOrdLines,BeanPropertyRowMapper.newInstance(OrderLine.class),orderId);
            order.setLines(orderLines);

            String dayOfWeek = new SimpleDateFormat("u").format(order.getOrderDate());
            String dayOfWeekPattern = "^" + dayOfWeek + ",|," + dayOfWeek + "$|," + dayOfWeek + ",";
            String dayOfMon = new SimpleDateFormat("d").format(order.getOrderDate());
            String dayOfMonPattern = "^" + dayOfMon + ",|," + dayOfMon + "$|," + dayOfMon + ",";

            List<Subscription> subscriptions = jdbcTemplate.query(SQLQuery.selSubLines, BeanPropertyRowMapper.newInstance(Subscription.class), new Object[]{order.getCustomer_id(), dayOfMonPattern, dayOfWeekPattern});
            order.setSubscriptions(subscriptions);

            // Add subscription information to order lines
            int i = -1;
            for (Subscription s : subscriptions) {
                order.getLines().add(new OrderLine(i--, s.getProduct_id(), s.getQuantity(), order.getId(), s.getId(), null));
            }

 
			List<ProductInCatalog> products = productJDBCRepository.getProductListing(order.getCustomer_id()).getProducts();
	            for (OrderLine ol : order.getLines()) {
	                ol.setProduct(products.stream().filter(p -> p.getId() == ol.getProduct_id()).findFirst().orElse(null));
	            }
			
            return order;
        } catch (DataAccessException ex) {
            throw new ApplicationException(0, "Failed to load order. " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}

	
	

	@Override
	public Order updateOrder(Integer id, Map<String, Object> subscription) throws ApplicationException {

		 try {
	            Order o = getById(id);

	            List<Map<String, Object>> lines = (List<Map<String, Object>>) subscription.get("lines");
	            
	            for (Map<String, Object> lineData : lines) {
	                
					Integer productId = (Integer)lineData.get("product_id");
	                Integer quantity =  (Integer)lineData.get("quantity");

	                // Find the existing order line for the given productId
	                OrderLine existingLine = o.getLines().stream()
	                    .filter(line -> line.getProduct().equals(productId))
	                    .findFirst()
	                    .orElse(null);

	                if (existingLine != null) {
	                    if (quantity > 0) {
	                        jdbcTemplate.update(SQLQuery.updOrdLn, quantity, existingLine.getId());
	                    } else {
	                        jdbcTemplate.update(SQLQuery.delOrdLn, existingLine.getId());
	                    }
	                } else {
	                    jdbcTemplate.update(SQLQuery.creOrdLine, id, productId, quantity);
	                }
	            }
	            return getById(id); 
		 }catch (Exception ex) {
	            logger.error("Failed to update Order.", ex);
	            throw new ApplicationException(0, "Failed to update order. " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
		
	}

	@Override
	public void deleteById(Integer id) throws ApplicationException {
        try {
            // Check if the subscription exists
            Order order = getById(id); // Assuming this method fetches the subscription by ID
            if (order == null) {
                throw new ApplicationException(0, "Subscription not found.", HttpStatus.BAD_REQUEST);
            }
            jdbcTemplate.update("DELETE FROM subscription WHERE id = ?", id);
        } catch (Exception ex) {
            logger.error("Failed to delete subscription.", ex);
            throw new ApplicationException(0, "Failed to delete subscription. " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
		
	}
	
	@Override
	public OrderLine getOrderLine(Integer cusId, Integer orderId, Integer lineId) throws ApplicationException {
		try {
			OrderLine orderLine = jdbcTemplate.queryForObject(SQLQuery.selOrdLine,BeanPropertyRowMapper.newInstance(OrderLine.class), orderId,lineId);

			if (orderLine == null) {
				throw new ApplicationException(0,
						String.format("Unable to find Order Line with orderId %d and lineId %d", orderId, lineId),
						HttpStatus.BAD_REQUEST);
			}

			List<ProductInCatalog> products = productJDBCRepository.getProductListing(cusId).getProducts();
			for (Product product : products) {
				if (product.getId().equals(orderLine.getProduct_id())) {
					orderLine.setProduct(product);
					break;
				}
			}
			return orderLine;

		} catch (EmptyResultDataAccessException e) {
			throw new ApplicationException(0,
					String.format("Unable to find Order Line with orderId %d and lineId %d", orderId, lineId),
					HttpStatus.BAD_REQUEST);
		} catch (Exception ex) {
			throw new ApplicationException(0, "Failed to load Order Line. " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}


	private boolean isEqual(Date orderDate1, Date orderDate2) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
		return sdf.format(orderDate1).equals(sdf.format(orderDate2));
	}

	private Date addDay(Date currentDate, int i) {
		 Calendar calendar = Calendar.getInstance();
		    calendar.setTime(currentDate);
		    calendar.add(Calendar.DAY_OF_MONTH, i);  // Adds the number of days
		    return calendar.getTime();
	}
}
