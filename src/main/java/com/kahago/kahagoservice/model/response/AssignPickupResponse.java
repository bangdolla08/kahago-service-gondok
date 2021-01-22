package com.kahago.kahagoservice.model.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Builder;
import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@Builder
@JsonSerialize
@JsonInclude(value=Include.NON_NULL)
public class AssignPickupResponse {
	private String customerName;
	private String customerId;
	private String addressPickup;
	private Integer qtyBook;
	private Integer totalItem;
	private Integer totalKg;
	private Integer totalVolume;
	private String telp;
	private Integer mostWeight;
	private Integer pickupAddresId;
	private String postalCode;
	private Integer statusAddress;
	private String totalBarangTitipan;
	private List<BookDataResponse> detailBook;
}
