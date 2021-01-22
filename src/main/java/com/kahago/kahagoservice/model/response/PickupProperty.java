package com.kahago.kahagoservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PickupProperty {
	private String pickupDate;
	private String pickupTime;
	private String pickupDriver;
	private String pickupStatus;
}
