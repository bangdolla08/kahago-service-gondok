package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@JsonSerialize
@JsonInclude(value=Include.NON_NULL)
public class BookResponseJet {
	private String rc;
	private String description;
	private String noResi;
	private String bookingCode;
	private String urlResi;
	private String insurance;
	private String fee;
	private String routingCode;
}
