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
public class BookRequestPop {
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
	private String receiverCity;
	private String receiverDistrict;
	private String receiverProvince;
	private String goodsDescription;
	private String notes;
	private String goodsPrice;
	private List<ItemsJet> items;
}
