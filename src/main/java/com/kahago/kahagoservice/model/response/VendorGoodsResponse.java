package com.kahago.kahagoservice.model.response;

import java.util.List;

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
public class VendorGoodsResponse {
	private Integer switcherCode;
	private String switcherName;
	private Integer productSwCode;
	private String productName;
	private List<GoodsRes> detailGoods;
}
