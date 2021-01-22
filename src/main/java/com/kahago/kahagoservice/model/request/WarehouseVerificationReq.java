package com.kahago.kahagoservice.model.request;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kahago.kahagoservice.entity.TBookEntity;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@JsonSerialize
public class WarehouseVerificationReq {
	private String userId;
	private String bookingCode;
	private Integer status;
	private BigDecimal extraCharge;
	private BigDecimal priceGoods;
	private BigDecimal amount;
	private Boolean isPack;
	private Boolean isInsurance;
	private BigDecimal insurance;
	private Double totalPackKg;
	private String qrcodeExt;
	private List<DetailBooking> detail;
}
