package com.org.tsd.models;

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
public class Address{
	private int id;
    private int customer_id;
    private String line1;
    private String line2;
    private String line3;
    private String pin_code;
    private String state_name;
    private String country;
    private String city;
    private String short_name;
    private String geo_tag;
    private boolean is_verified;
    private boolean is_default;
}