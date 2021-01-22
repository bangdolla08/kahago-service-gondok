package com.kahago.kahagoservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DimensiResp {
	private String bookingCode;
	private String length;
	private String width;
	private String height;
	private String volumeWeight;
}
