package com.kahago.kahagoservice.client.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

@Data
@JsonSerialize
@JsonInclude(value = Include.NON_NULL)
public class ResponseModel {
	@JsonProperty("tiketId")
    private String tiketId;
	@JsonProperty("bookingId")
    private String bookingId;
	@JsonProperty("status")
    private String status;
	@JsonProperty("paymentStatusCode")
    private Integer paymentStatusCode;
	@JsonProperty("paymentStatus")
    private String paymentStatus;
	@JsonProperty("paymentDate")
    private String paymentDate;
	@JsonProperty("redirectUrl")
    private String redirectUrl;
	private String deeplink;
	@JsonProperty("fraudStatus")
    private String fraudStatus;
	@JsonProperty("idPayment")
    private String idPayment;
	private String fromCode;
	private String toCode;
}
