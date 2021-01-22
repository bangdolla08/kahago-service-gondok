package com.kahago.kahagoservice.client.model.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

@Data
@JsonSerialize
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class NotificationFaspay {
	private String request;
	private String trxId;
	private String merchantId;
	private String merchant;
	private String billNo;
	private String paymentReff;
	private String paymentDate;
	private String paymentStatusCode;
	private String paymentStatusDesc;
	private String billTotal;
	private String paymentTotal;
	private String paymentChannelUid;
	private String paymentChannel;
	private String signature;
}
