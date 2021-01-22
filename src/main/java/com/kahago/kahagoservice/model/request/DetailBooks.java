package com.kahago.kahagoservice.model.request;

import lombok.Data;

@Data
public class DetailBooks {
	private String bookingCode;
	private String qrcode;
	private String typeTrx;//1.Payment, 2.Request Pickup
}
