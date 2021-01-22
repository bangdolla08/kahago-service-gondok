package com.kahago.kahagoservice.model.response;

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
public class DetailManifestRequestPickup {
	private String vendorName;
	private String productName;
	private String jumlahLembar;
	private String weight;
	private String destination;
	private String ReceiverName;
	private String qrcode;
	private String imageVendor;
	private String qrcodeExt;
	private Integer requestStatus;
	
}
