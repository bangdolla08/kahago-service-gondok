package com.kahago.kahagoservice.model.response;


import java.util.List;

import org.springframework.boot.context.properties.bind.DefaultValue;

import lombok.Builder.Default;
import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
public class LeadTimeProductResponse {
	private Integer productCode;
	private String productName;
	private List<LeadTimeDtlResponse> detailStatus;
}
