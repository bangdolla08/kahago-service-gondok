package com.kahago.kahagoservice.model.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ibnu Wasis
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize
@JsonInclude(value=Include.NON_NULL)
public class ManifestPickupResponse {
	private String noManifest;
	private String pickupDate;
	private String pickupTime;
	private String customerName;
	private String customerTelp;
	private Integer statusCode;
	private String statusDesc;
	private String countAssign;
	private String linkManifest;
	private String latitude;
	private String longitude;
	private String address;
	private String addressNote;
	private String kelurahan;
	private String kecamatan;
	private String kota;
	private String provinsi;
	private String postalCode;
	private String flag;
	private Integer idPostalCode;
	private Integer pickupAddressId;
	private List<DetailManifestPickup> detail;
	private String lastUpdateManifestAt;
	private String lastUpdatePickupAt;
	private List<DetailManifestRequestPickup> detailReq;
	private Boolean isBooking;
	private Boolean isDelete;
	private Boolean isAdd;
	
}
