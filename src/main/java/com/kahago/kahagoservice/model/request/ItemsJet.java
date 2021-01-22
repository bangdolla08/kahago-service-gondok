package com.kahago.kahagoservice.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@JsonSerialize
@JsonInclude(value=Include.NON_NULL)
public class ItemsJet {
	private String weight;
	private String height;
	private String width;
	private String length;
	private String packagingCode;
	private String packagingQty;
	private String packagingFee;
	private String fee;
}
