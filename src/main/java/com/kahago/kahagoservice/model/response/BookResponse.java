package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.PropertyNamingStrategyBase;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 19 Nov 2019
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
@JsonInclude(value=Include.NON_NULL)
public class BookResponse {
	private String rc;
	private String desc;
	private String bookingCode;
	private String origin;
	private String destination;
	private String senderName;
	private String senderAddress;
	private String senderTelp;
	private String senderEmail;
	private String receiverName;
	private String receiverAddress;
	private String receivertelp;
	private String comodity;
	private String amount;
	private String surcharge;
	private String extracharge;
	private String ppn;
	private String totalAmount;
	@JsonProperty("user_id")
	private String officerId;
	private String trxDate;
	private String stt;
	private String timeLimit;
	private String urlResi;
}
