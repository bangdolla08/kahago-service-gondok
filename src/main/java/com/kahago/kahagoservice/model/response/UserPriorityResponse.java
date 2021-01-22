package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ibnu Wasis
 */
@Data
@Builder
@JsonSerialize
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value=Include.NON_NULL)
public class UserPriorityResponse {
	private Integer seqid;
	@JsonProperty(value="request_1")
    private Boolean request1;
    @JsonProperty(value="request_2")
    private Boolean request2;
    @JsonProperty(value="request_3")
    private Boolean request3;
    private Integer minKiriman;
    private Boolean paylater;
    private Boolean isResiAuto;
}
