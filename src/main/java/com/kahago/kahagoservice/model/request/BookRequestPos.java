package com.kahago.kahagoservice.model.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@JsonSerialize
@JsonInclude(value=Include.NON_NULL)
public class BookRequestPos {
	private String bookCode;
	private String origin;
	private String destination;
	private String productCode;
	private String senderName;
	private String senderAddress;
	private String senderPhone;
	private String senderEmail;
	private String senderPostalCode;
	private String senderCity;
	private String senderSubdistrict;
	private String senderCountry;
	private String receiverName;
	private String receiverAddress;
	private String receiverPhone;
	private String receiverEmail;
	private String receiverPostalCode;
	private String receiverCity;
	private String receiverProvince;
	private String receiverSubDistrict;
	private String receiverCountry;
	private String goodsDescription;
	private String notes;
	private String goodsPrice;
	private String modaId;
	private String quantity;
	private String weight;
	private String volume;
	@JsonProperty("trx_date")
	private String trxDate;
	private String discount;
	private String userid;
	private String agenid;
	private String manifestnumber;
	private String transref;
	private String sign;
	private List<ItemsJet> items;
}
