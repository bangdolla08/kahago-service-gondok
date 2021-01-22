package com.kahago.kahagoservice.model.request;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
public class DestinationRequest {
	private Integer id;
	private String priceAreaCode;
	private String bookingAreaCode;
	private Integer idPostalCode;
	private Integer switcherCode;
	private Integer areaId;
}
