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
@JsonInclude(value=Include.NON_NULL)
@JsonSerialize
public class BookRequestSicepat {
	@JsonProperty("reference_number")
	private String referenceNumber;
	@JsonProperty("pickup_merchant_code")
	private String pickupMerchantCode;
	@JsonProperty("pickup_request_date")
	private String pickupRequestDate;
	@JsonProperty("pickup_merchant_name")
	private String pickupMerchantName;
	@JsonProperty("pickup_address")
	private String pickupAddress;
	@JsonProperty("pickup_city")
	private String pickupCity;
	@JsonProperty("pickup_merchant_phone")
	private String pickupMerchantPhone;
	@JsonProperty("pickup_merchant_email")
	private String pickupMerchantEmail;
	private String notes;
	private List<PackageListItem> datas;
}
