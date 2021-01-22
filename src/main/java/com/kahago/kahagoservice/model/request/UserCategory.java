package com.kahago.kahagoservice.model.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@JsonSerialize
public class UserCategory {
	private Integer userCategoryId;
}
