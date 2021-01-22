package com.kahago.kahagoservice.model.response;
/**
 * @author Ibnu Wasis
 */

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize
@JsonInclude(value=Include.NON_NULL)
public class ManifestPickup {
	private String noManifest;
	private String timePickup;
	private Integer statusCode;
	private String statusDesc;
	private String vendor;
	private String productVendor;
	private String imageVendor;
	private String courierName;
	private Integer bookingInCourier;
	private Integer bookingInWarehouse;
	private Integer totalItemBooking;
	private String jumlahBarang;
	private Integer sumAmount;
	private String 	totalBooking;
	private String courierId;
	private String belumTerima;
	private String pickupDate;
	private Integer jumlahTitik;
	private List<DetailManifestPickup> detail;
}
