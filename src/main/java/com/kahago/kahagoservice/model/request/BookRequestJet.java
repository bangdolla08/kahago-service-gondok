package com.kahago.kahagoservice.model.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@JsonSerialize
@JsonInclude(value=Include.NON_NULL)
public class BookRequestJet {
	private String bookCode;
	private String origin;
	private String destination;
	private String productCode;
	private String senderName;
	private String senderAddress;
	private String senderPhone;
	private String senderEmail;
	private String senderPostalCode;
	private String receiverName;
	private String receiverAddress;
	private String receiverPhone;
	private String receiverEmail;
	private String receiverPostalCode;
	private String goodsDescription;
	private String notes;
	private String goodsPrice;
	private String isInsurance;
	private String stt;
	private List<ItemsJet> items;
	//pickup address
	private String pickupName;
	private String pickupAddress;
	private String pickupPhone;
	private String pickupEmail;
	private String pickupPostalCode;
	
	private String apiKey;
	private String partnerId;
	
}

