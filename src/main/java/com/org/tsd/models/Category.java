package com.org.tsd.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Category {
    private Integer id;
    private String name;
    @JsonProperty("parentId")
    private Integer parentId;
    @JsonProperty("categoryImage")
    private String imageUrl;
}
