package com.kahago.kahagoservice.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
public class HistoryTopUpResponse {
	private Integer seq;
	private String noTiket;
	private String desc;
	private BigDecimal nominal;
	private String bankTransfer;
	private String noRekTransfer;
	private String status;
	private String tglTrx;
}
