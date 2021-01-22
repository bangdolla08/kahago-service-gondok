package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

@Data
@JsonSerialize
@JsonInclude(value=Include.NON_NULL)
public class PaylaterList {
	private String seq;
	private String bookingCode;
	private String shipperName;
	private String receiverName;
	private String amount;
	private String remainingTime;
	private String statusCode;
	private String statusDesc;
}
