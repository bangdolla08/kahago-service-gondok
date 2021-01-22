package com.kahago.kahagoservice.model.request;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@JsonSerialize
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PermohonanListReq extends PageHeaderRequest{
	@ApiModelProperty(value="0. updatable, 1. fix, 2.print, 3. request Open")
	private List<Integer> status;
	private List<Integer> vendorCode;
	private String bookId;
	private String noPermohonan;
}
