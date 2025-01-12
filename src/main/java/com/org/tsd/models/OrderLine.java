package com.org.tsd.models;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderLine {
	private int id;
    private int productId;      
    private int quantity;
    private int orderId;       
    private int subscriptionId; 
    private Product product;
    
    public OrderLine(Map<String, Object> row) {
        this.id = (Integer) row.get("id");
        this.orderId = (Integer) row.get("order_id");
        this.productId = (Integer) row.get("product_id");
        this.quantity = (Integer) row.get("quantity");
    }
}