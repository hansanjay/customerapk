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

public class Product {
    public Integer id;
    public Double rating;
    @JsonProperty("brandId")
    public Integer brand_id;
    public String title;
    public String description;
    public String features;
    @JsonProperty("shelfLife")
    public String shelf_life;
    @JsonProperty("unitDisplay")
    public String unit_display;
    public Integer unit;
    public String unit_type;
    @JsonProperty("weightDisplay")
    public String weight_display;
    @JsonProperty("weight_(g)")
    public Integer weight_g;
    public float mrp;
    public String image_url;
    @JsonProperty("type")
    public String prd_type;
    @JsonProperty("returnPolicy")
    public String return_policy;
    public String product_class;
    public String packaging_type;
    @JsonProperty("productGroup")
    public String product_group;
    public String category;
    @JsonProperty("subCategory")
    public String sub_category;
}