package com.kahago.kahagoservice.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@JsonSerialize
@JsonInclude(value=Include.NON_NULL)
public class ApprovalRejectWarehouseReq extends PageHeaderRequest{
	@ApiModelProperty(value="User ID")
	private String userId;
	@ApiModelProperty(value="Booking Code")
	private String bookId;
	@ApiModelProperty(value="Vendor ID")
	private Integer vendorId;
	@ApiModelProperty(value="Office Code",required=true)
	private String officeCode;
	@ApiModelProperty(value="Id Warehouse untuk di terima")
	private Integer idWarehouseDetail;
	private Boolean isConfirmRejectApprove;
}
