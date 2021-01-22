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
public class AppBookingRequest extends PageHeaderRequest{
	private String userId;
	private String bookingCode;
	private String code;
	private Integer status;
	private String searchString;
	private Integer switcherCode;
}
