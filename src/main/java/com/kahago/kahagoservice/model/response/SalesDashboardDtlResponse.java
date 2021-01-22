package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Builder;
import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@JsonSerialize
@Builder
@JsonInclude(value=Include.NON_NULL)
public class SalesDashboardDtlResponse {
	private String userId;
	private String office;
	private String trxDate;
	private Integer totalTrx;
	private Integer totalRevenue;
	private Integer totalVendor;
	private Integer totalNewUser;
}
