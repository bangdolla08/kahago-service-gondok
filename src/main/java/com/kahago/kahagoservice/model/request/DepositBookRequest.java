package com.kahago.kahagoservice.model.request;

import java.util.List;

import lombok.Data;

@Data
public class DepositBookRequest {
	private String userId;
	private List<String> qrcodes;
	private String officeCode;
}
