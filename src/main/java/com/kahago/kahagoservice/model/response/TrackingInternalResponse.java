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
@JsonInclude(value=Include.NON_NULL)
@Builder
public class TrackingInternalResponse {
	private Integer seq;
	private String tanggal;
	private String userId;
	private String userProcess;
	private String userProcessName;
	private String bookingCode;
	private String noResi;
	private String weight;
	private String volumeWeight;
	private String amount;
	private String statusDesc;
	private Integer status;
}
