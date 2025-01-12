package com.org.tsd.models;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Order {
	private int id;
    private int customerId;
    private int distributorId;
    private int addressId;
    private Date orderDate; 
    private String status;
    private List<OrderLine> lines;
    private List<Subscription> subscriptions;
    
    public Order(Map<String, Object> row) {
        this.id = (Integer) row.get("id");
        this.customerId = (Integer) row.get("customer_id");
        this.orderDate = (Date) row.get("order_date");
        this.distributorId=(Integer) row.get("distributorId");
        this.addressId=(Integer) row.get("order_date");
        this.status=(String) row.get("addressId");
    }

    
}
