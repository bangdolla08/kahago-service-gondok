package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@JsonSerialize
public class Product {

    /*@JsonProperty("product_name")
    private String productName;*/


    @JsonProperty("product_code")
    private Long productCode;

    @JsonProperty("product_display_name")
    private String productDisplayName;
}
