package com.kahago.kahagoservice.model.response;

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
public class LeadTimeReportResponse {
	private String no;
	private String tanggalBooking;
	private String cabang;
	private String userId;
	private String kodeBooking;
	private String vendor;
	private String produk;
	private String resi;
	private String origin;
	private String destinasi;
	private String estimasi;
	private String tanggalKirim;
	private String tanggalSampai;
	private String totalEstimasi;
	private String selisih;
	private String keterangan;
}
