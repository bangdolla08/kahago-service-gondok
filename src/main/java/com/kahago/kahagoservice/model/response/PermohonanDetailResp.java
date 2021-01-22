package com.kahago.kahagoservice.model.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class PermohonanDetailResp {
	private Integer seqid;
	private Integer idPermohonan;
	private String noPermohonan;
	private String bookId;
	private String stt;
	private String trxDate;
	private String vendor;
	private String product;
	private String totalWeight;
	private String totalPrice;
	private String totalHpp;
	private String totalHppActual;
	private String invoiceVendor;
	private String priceVendor;
	private String destination;
	private Integer status;
	private List<DetailPermohonan> oldPayment;
}
