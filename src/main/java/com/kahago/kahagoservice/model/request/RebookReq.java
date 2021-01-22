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
public class RebookReq {
	private String bookingCode;
	private String userId;
	private Integer pickupTimeId;
	private String pickupDate;

}
