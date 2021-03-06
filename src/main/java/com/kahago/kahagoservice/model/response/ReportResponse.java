package com.kahago.kahagoservice.model.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Builder;
import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@Builder
@JsonSerialize
@JsonInclude(value=Include.NON_NULL)
public class ReportResponse {
	private Integer vendorCode;
	private String vendorName;
	private LocalDate trxDate;
	private Long totalTrx;
	private Long totalWeight;
}
