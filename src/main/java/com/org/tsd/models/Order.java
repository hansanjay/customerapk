package com.org.tsd.models;

import java.util.ArrayList;
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
    private int customer_id;
    private int distributor_id;
    private int address_id;
    private Date orderDate; 
    private String status;
    private List<OrderLine> lines;
    private List<Subscription> subscriptions = new ArrayList<Subscription>();
    
    public Order(Map<String, Object> row) {
        this.id = (Integer) row.get("id");
        this.customer_id = (Integer) row.get("customer_id");
        this.orderDate = (Date) row.get("order_date");
        this.distributor_id=(Integer) row.get("distributorId");
        this.address_id=(Integer) row.get("order_date");
        this.status=(String) row.get("addressId");
    }

    
}
