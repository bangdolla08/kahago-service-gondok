package com.kahago.kahagoservice.model.response;

import java.util.List;

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
public class TotalTrxResponse {
	private List<Integer> status;
	private String statusDesc;
	private String startDate;
	private String endDate;
	private Integer totalTrxBooking;
	private Integer totalTrxRequest;
	private Integer totalAllTrx;
	private String userId;
}
