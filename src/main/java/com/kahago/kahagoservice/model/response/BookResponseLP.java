package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ibnu Wasis
 */
@Data
@JsonSerialize
@NoArgsConstructor
public class BookResponseLP {
	private String rc;
	private String rd;
    @JsonProperty("orderNo")
    private String orderNo;

    @JsonProperty("packageId")
    private String packageId;

    @JsonProperty("packageDate")
    private String packageDate;
}
