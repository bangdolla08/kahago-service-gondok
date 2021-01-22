package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Builder;
import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@Builder
@JsonSerialize
public class HistoryVisitResponse {
	private String url;
	private String param;
	private String action;
	private String userId;
	private Integer flag; 
}
