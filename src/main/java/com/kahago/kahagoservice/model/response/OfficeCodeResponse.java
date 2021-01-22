package com.kahago.kahagoservice.model.response;

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
public class OfficeCodeResponse {
	private String officeCode;
	private String parrentOffice;
	private String name;
	private String unitType;
	private String address;
	private String city;
	private String postalCode;
	private String telp;
	private String fax;
	private String statusLayanan;
}
