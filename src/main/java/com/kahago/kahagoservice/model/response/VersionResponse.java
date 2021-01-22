package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@JsonSerialize
public class VersionResponse {
	private String version;
	private String flag;
}
