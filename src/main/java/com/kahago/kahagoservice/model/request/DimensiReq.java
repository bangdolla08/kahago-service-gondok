package com.kahago.kahagoservice.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DimensiReq {
	private String bookingCode;
	private String length;
	private String width;
	private String height;
}
