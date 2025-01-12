package com.org.tsd.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductInCatalog extends Product {
    String brand;
    @JsonProperty("lineId")
    int line_id;
    @JsonProperty("distributorId")
    int distributor_id;
    @JsonProperty("catalogId")
    int catalog_id;
    float discount;
    float price ;
}
