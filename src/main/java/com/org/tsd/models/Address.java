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
    private int customerId;
    private String line1;
    private String line2;
    private String line3;
    private String pinCode;
    private String stateName;
    private String country;
    private String city;
    private String shortName;
    private String geoTag;
    private boolean isVerified;
    private boolean isDefault;
}