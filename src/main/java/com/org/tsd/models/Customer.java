package com.org.tsd.models;

import java.util.List;

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
public class Customer{
    private int id;
    private int distributor_id;
    private String mobile;
    private String email;
    private String first_name;
    private String last_name;
    private boolean active;
    private List<Address> addressList;
}