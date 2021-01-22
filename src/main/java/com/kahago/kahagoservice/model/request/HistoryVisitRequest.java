package com.kahago.kahagoservice.model.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@JsonSerialize
public class HistoryVisitRequest {
	private String url;
	private String param;
	private String action;
	private String userId;
	private Integer flag;
}
