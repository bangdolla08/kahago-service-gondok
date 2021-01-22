package com.kahago.kahagoservice.model.request;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@JsonSerialize
public class RequestBook extends PageHeaderRequest{
	@ApiModelProperty(value="0=Book List, 1=Verifikasi Gudang List, 2=Barang Datang List")
	@NotEmpty
	private Integer modul;
	private List<Integer> status;
	private String filter;
	private List<String> officeCode;
	private String userId;
	private List<Integer> vendorCode;
	private String qrCode;
	private String origin;
}
