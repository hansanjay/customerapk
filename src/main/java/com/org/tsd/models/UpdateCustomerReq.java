package com.org.tsd.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCustomerReq{
    private String email;
    private String first_name;
    private String last_name;
    private boolean isDefault;
    private List<Address> addressList;
}