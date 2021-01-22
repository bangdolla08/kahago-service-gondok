package com.kahago.kahagoservice.model.response;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@JsonSerialize
@JsonInclude(value=Include.NON_NULL)
public class DeliveryResponse {
	private Integer seq;
	private String bookingCode;
	private String senderName;
	private String receiverName;
	private BigDecimal amount;
	private String qrcode;
	private String stt;
	private String urlresi;
	private String trxDate;
	private Boolean isBooking;

}
