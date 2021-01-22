package com.kahago.kahagoservice.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@JsonSerialize
@JsonInclude(value=Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BookRequestIndah {
	private String bookingCode;
	private String noResi;
	private String idModa;
	private String origin;
	private String destination;
	private String comodity;
	private String grossWeight;
	private String amount;
	private String senderName;
	private String senderTelp;
	private String senderAddress;
	private String receiverName;
	private String receiverTelp;
	private String receiverAddress;
	private String jmlBarang;
}
