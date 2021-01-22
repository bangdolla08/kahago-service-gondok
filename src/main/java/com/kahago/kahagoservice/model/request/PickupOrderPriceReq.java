package com.kahago.kahagoservice.model.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

@Data
@JsonSerialize
public class PickupOrderPriceReq {
	private String pickupOrderId;
	private String idKecamatan;
	private Integer weight;
	private String vendorCode;
	private Integer originId;
	private Long comodityId;
	private String officeCode;
}
