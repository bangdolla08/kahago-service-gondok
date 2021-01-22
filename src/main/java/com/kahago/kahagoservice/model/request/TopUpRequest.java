package com.kahago.kahagoservice.model.request;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@JsonSerialize
public class TopUpRequest {
	private Integer bankDepCode;
	private BigDecimal nominal;
	private String decription;
	private List<UserDetail> userId;
}
