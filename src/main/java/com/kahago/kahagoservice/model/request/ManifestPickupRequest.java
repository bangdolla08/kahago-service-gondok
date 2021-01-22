package com.kahago.kahagoservice.model.request;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@JsonSerialize
@JsonInclude(value=Include.NON_NULL)
public class ManifestPickupRequest {
	@ApiModelProperty(value="User Id From user Login", required = true)
	@NotEmpty(message="please provide User ID ")
	private String userId;
	@ApiModelProperty(value="Manifest Number")
	private String noManifest;
	@ApiModelProperty(value="Status Code")
	private Integer statusCode;
	@ApiModelProperty(value="User Id Customer")
	private String customerId;
	@ApiModelProperty(value="Booking Code")
	private String bookingCode;
	@ApiModelProperty(value="Pickup Address ID")
	private Integer pickupAddressId;
	@ApiModelProperty(value="QRCode Ext")
	private String qrCodeExt;
	private String pickupOrderId;
}
