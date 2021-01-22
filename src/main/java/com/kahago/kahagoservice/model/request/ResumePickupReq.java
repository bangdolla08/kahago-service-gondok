package com.kahago.kahagoservice.model.request;
/**
 * @author Ibnu Wasis
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@JsonSerialize
@JsonInclude(value=Include.NON_NULL)
public class ResumePickupReq {
	private String customerName;
	private String noManifest;
	@ApiModelProperty(required=true)
	private String userId;
	private String bookingCode;
	@ApiModelProperty(required=true)
	private String startDate;
	@ApiModelProperty(required=true)
	private String endDate;
}
