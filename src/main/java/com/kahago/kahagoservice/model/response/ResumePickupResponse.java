package com.kahago.kahagoservice.model.response;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class ResumePickupResponse {
	private Integer seq;
	private String customerName;
	private String address;
	private String kelurahan;
	private String kecamatan;
	private String addressNote;
	private String customerId;
	private String kota;
	private String postalCode;
	private String bookingCode;
	private String qty;
	private String volume;
	private String weight;
	private String isInsurance;
	private String noManifest;
	private String statusDesc;
	private String qrcode;
	private String vendor;
	private String productDisplayName;
	private String imagesVendor;
	private String courierId;
	private String isPacking;
	private String customerTelp;
	private List<DimensiGoods> lbooks;
	private String pathImage;
	private String linkManifest;
	private String pickupTime;
	private LocalDate pickupDate;
	private Integer status;
	private Integer statusPickup;
	private Long productSwCode;
	private String statusPickupDesc;
	private Integer statusManifest;
	private Boolean isBooking;

}
