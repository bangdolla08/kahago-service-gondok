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
public class MonitorManifestResponse {
	private Integer totalPickup;
	private String pickupTime;
	private String pickupDate;
	private Integer pickupTimeId;
}
