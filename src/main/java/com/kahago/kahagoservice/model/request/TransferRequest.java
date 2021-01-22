package com.kahago.kahagoservice.model.request;

import lombok.Data;

@Data
public class TransferRequest {
	private String userId;
	private String uniqNumber;
	private String statusUniq;
	private String totalNominal;
}
