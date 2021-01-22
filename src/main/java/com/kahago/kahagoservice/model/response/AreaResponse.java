package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ibnu Wasis
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AreaResponse {
	@JsonProperty(value="fromCode")
	private String fromCode;
	@JsonProperty(value="toCode")
    private String toCode;
}
