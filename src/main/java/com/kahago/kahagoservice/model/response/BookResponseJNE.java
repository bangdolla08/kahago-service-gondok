package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@JsonSerialize
public class BookResponseJNE {
	private String rc;
    private String description;
    private String noResi;
    private String bookingCode;
    private String routingCode;
}
