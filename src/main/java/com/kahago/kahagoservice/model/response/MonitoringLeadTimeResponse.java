package com.kahago.kahagoservice.model.response;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@JsonSerialize
public class MonitoringLeadTimeResponse {
	private Integer switcherCode;
	private String vendorName;
	private String switcherImage;
	private List<LeadTimeProductResponse> detailProduct;
	private List<LeadTimeDtlResponse> totalVendor;
}
