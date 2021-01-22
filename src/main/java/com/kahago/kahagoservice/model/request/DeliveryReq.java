package com.kahago.kahagoservice.model.request;

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
public class DeliveryReq {
	@ApiModelProperty(value="pending OR final",required=true)
	private String status;
	@ApiModelProperty(value="user Id",required=true)
	private String userId;
	@ApiModelProperty(value="Key of Search")
	private String cari;
	@ApiModelProperty(value="Page Number")
	private Integer page;
	@ApiModelProperty(value="insert 0 if ViewAll, 1 if SelfSender,2 if SelfReceiver,3 if UserId")
	private Integer filterby;
	@ApiModelProperty(value="insert userId Search")
	private String idSearch;
}
