package com.kahago.kahagoservice.model.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@JsonSerialize
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BookRequestBukaSend {
	private String bookingCode;
	private String comodity;
	private Integer grossWeight;
	private String amount;
	private String senderName;
	private String senderTelp;
	private String senderAddress;
	private String receiverName;
	private String receiverTelp;
	private String receiverAddress;
	private Integer jmlBarang;
	private String CityFrom;
	private String CityTo;
	private String AreaFrom;
	private String AreaTo;
	private Double insured;
	private Double priceAdjustment;
	private Double price;
	private Double extraCharge;
	private String provinceFrom;
	private String postalCodeFrom;
	private String provinceTo;
	private String postalCodeTo;
	private String courier;
	private String senderEmail;
	private String receiverEmail;
	private String note;
	private Integer width;
	private Integer length;
	private Integer height;
}
