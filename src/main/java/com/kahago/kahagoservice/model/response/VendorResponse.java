package com.kahago.kahagoservice.model.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ibnu Wasis
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize
@JsonInclude(value=Include.NON_NULL)
public class VendorResponse {
	private Integer swicherCode;
	private String name;
	private String displayName;
	private String images;
	private List<Product> product;
}
