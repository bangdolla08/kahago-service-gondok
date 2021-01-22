package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kahago.kahagoservice.model.response.Product.ProductBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ibnu Wasis
 */
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@JsonSerialize
public class ProductResponse {
	@JsonProperty("product_code")
    private Long productCode;

    @JsonProperty("product_display_name")
    private String productDisplayName;
}
