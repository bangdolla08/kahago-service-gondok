package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
public class OutgoingCounterResp {
	private Integer idOutgoingCounter;
	private String codeOutgoingCounter;
	private String officeCode;
	private String officeName;
	private String courierId;
	private String courierName;
	
}
