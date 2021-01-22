package com.kahago.kahagoservice.model.response;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kahago.kahagoservice.entity.TBookEntity;

import lombok.Builder;
import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@JsonSerialize
@JsonInclude(value=Include.NON_NULL)
@Data
@Builder
public class WarehouseVerificationResponse {
	private Integer seq;
	private String userId;
	private String destination;
	private String bookId;
	private String goodDesc;
	private Integer jumlahLembar;
	private Long volume;
	private Long Weight;
	private String vendorName;
	private String productName;
	private String statusDesc;
	private String officeName;
	private BigDecimal nominal;
	private List<TBookEntity> books;
}
