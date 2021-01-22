package com.kahago.kahagoservice.model.request;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@JsonSerialize
public class LeadTimeRequest extends PageHeaderRequest{
	private Integer productCode;
	@ApiModelProperty(required=true)
	private Integer vendorCode;
	@ApiModelProperty(required=true)
	private String status;
	private String startDate;
	private String endDate;
	private String userId;
	private String areaId;
	private String bookingCode;
}
