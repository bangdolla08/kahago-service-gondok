package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Builder;
import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@Builder
@JsonSerialize
public class TotalBarangInCourierResponse {
	private String userId;
	private String name;
	private Integer totalBarang;
	private Integer totalBarangAssign;
}
