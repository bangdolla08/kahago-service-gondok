package com.kahago.kahagoservice.model.request;
/**
 * @author Ibnu Wasis
 */

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

@Data
@JsonSerialize
public class TokenRequest {
	private String userId;
	private String token;
}
