package com.kahago.kahagoservice.model.request;
/**
 * @author Ibnu Wasis
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

@Data
@JsonSerialize
@JsonInclude(value=Include.NON_NULL)
public class HistoryTopUpReq {
	private String userId;
	private String startDate;
	private String endDate;
}
