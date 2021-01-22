package com.kahago.kahagoservice.model.response;
/**
 * @author Ibnu Wasis
 */

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
@JsonSerialize
@JsonInclude(value=Include.NON_NULL)
public class RespTransferRespone {
	private String sender;
	private String note;
	private BigDecimal debit;
	private BigDecimal kredit;
	private String tglTransaksi;
}
