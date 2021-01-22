package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ibnu Wasis
 */
@Data
@JsonSerialize
@NoArgsConstructor
public class BookResponsePCP {
	private String awbNo;
	private String msg;
	private Boolean status;
}
