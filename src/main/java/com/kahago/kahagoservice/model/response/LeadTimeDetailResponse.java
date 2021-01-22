package com.kahago.kahagoservice.model.response;
/**
 * @author Ibnu Wasis
 */

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Builder;
import lombok.Data;

@Data
@JsonSerialize
@Builder
public class LeadTimeDetailResponse {
	private String bookingCode;
	private String userId;
	private String productName;
	private String vendor;
	private String resi;
	private String origin;
	private String destination;
	private String receiverName;
	private String tanggalBooking;
	private String estimasi;
	private String tanggalKirim;
	private String tanggalSampai;
	private String dueDays;
	private String branchName;
	private String estimasiReal;
}
