package com.kahago.kahagoservice.client.model.response;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

@Data
@JsonSerialize
public class RespTransfer {
	private Integer id;
	private Integer moduleid;
	private Integer date;
	private String sender;
	private String note;
	private BigDecimal debit;
	private BigDecimal kredit;
	private String hide;
	private String md5;
}
