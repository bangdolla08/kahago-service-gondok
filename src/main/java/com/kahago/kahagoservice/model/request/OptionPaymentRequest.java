package com.kahago.kahagoservice.model.request;

import java.util.List;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
public class OptionPaymentRequest {
	private Integer id;
	private String code;
	private String description;
	private ImageRequest image;
	private Integer codeVendor;
	private String operator;
	private Boolean isPhone;
	private Boolean isActive;
	private Integer minNominal;
	private Boolean isDeposit;
	private Boolean isPayment;
	private String offTimeStart;
	private String offTimeEnd;
	private List<UserCategory> userCategory;
}
