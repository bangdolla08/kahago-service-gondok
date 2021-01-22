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
@Builder
@JsonSerialize
@JsonInclude(value=Include.NON_NULL)
public class OptionPaymentResponse {
	private Integer seqid;
	private String codePayment;
	private String description;
	private String images;
	private String codeVendor;
	private String operator;
	private Boolean isPhone;
	private Boolean isActive;
	private Integer minNominal;
	private Boolean isDeposit;
	private Boolean isPayment;
	private String offTimeStart;
	private String offTimeEnd;
	
}
