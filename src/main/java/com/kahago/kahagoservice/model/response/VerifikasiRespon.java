package com.kahago.kahagoservice.model.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Builder;
import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@Builder
@JsonSerialize
@JsonInclude(value=Include.NON_NULL)
public class VerifikasiRespon {
	private String bookingCode;
	private String origin;
	private String destination;
	private String vendorName;
	private String productName;
	private String senderName;
	private String receiverName;
	private String extraCharge;
	private String insurance;
	private String totalAmount;
	private String lastTotalAmount;
	private String payType;
	private String urlResi;
	private String userId;
	private String amount;
	private List<TbookDetailHistory> books;
}
