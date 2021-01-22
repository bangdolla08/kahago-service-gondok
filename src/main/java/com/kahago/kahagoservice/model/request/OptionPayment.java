package com.kahago.kahagoservice.model.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@JsonSerialize
public class OptionPayment {
	private Integer optionPaymentId;
	private Boolean isDeposit;
	private Boolean isPayment;
}
