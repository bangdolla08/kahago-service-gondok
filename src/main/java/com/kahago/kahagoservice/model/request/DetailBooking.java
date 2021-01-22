package com.kahago.kahagoservice.model.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 19 Nov 2019
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailBooking {
	
	public DetailBooking(double vol, Double grossWeight2, Double length2, Double width2, Double height2) {
		// TODO Auto-generated constructor stub
		this.volume = vol;
		this.grossWeight = grossWeight2;
		this.length = length2;
		this.width = width2;
		this.height = height2;
	}
	private String seq;
	private Double length;
	private Double width;
	private Double height;
	private Double grossWeight;
	private Double volume;
}
