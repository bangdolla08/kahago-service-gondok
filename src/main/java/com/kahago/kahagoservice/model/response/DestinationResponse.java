package com.kahago.kahagoservice.model.response;

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
public class DestinationResponse {
	private Integer id;
	private String priceAreaCode;
	private String bookingAreaCode;
	private String vendor;
	private String postalCode;
	private String kelurahan;
	private String kecamatan;
	private String kota;
	private String provinsi;
	private Integer idPostalCode;
	private Integer vendorId;
}
