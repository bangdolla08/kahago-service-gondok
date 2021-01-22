package com.kahago.kahagoservice.model.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@JsonSerialize
@JsonInclude(value=Include.NON_NULL)
public class BookResponseSicepat {
	@JsonProperty("status")
    private String status;

	@JsonProperty("error_message")
	private String errorMessage;

	@JsonProperty("request_number")
	private String requestNumber;

    @JsonProperty("receipt_datetime")
    private String receiptDatetime;
    
    @JsonProperty("datas")
	private List<DatasItem> datas;
}
