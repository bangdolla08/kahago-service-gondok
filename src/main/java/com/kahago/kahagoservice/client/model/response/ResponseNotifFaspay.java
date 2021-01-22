package com.kahago.kahagoservice.client.model.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonSerialize
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ResponseNotifFaspay {
	private String response;
	private String trxId;
	private String merchantId;
	private String merchant;
	private String billNo;
	private String responseCode;
	private String responseDesc;
	private String responseDate;
}
