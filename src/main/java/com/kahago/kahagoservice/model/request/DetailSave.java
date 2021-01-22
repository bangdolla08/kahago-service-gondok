package com.kahago.kahagoservice.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonSerialize
@JsonInclude(value = Include.NON_NULL)
public class DetailSave {
	private String idPermohonan;
	private String bookId;
	private String invoiceVendor;
	private String priceVendor;
	private String status;
	private String description;
	private Boolean isNew;
}
