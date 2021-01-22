package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Hendro yuwono
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
@Builder
public class StatusResponse {
	
    public StatusResponse(String message, Boolean isActive, String status, String urlPrint) {
		super();
		this.message = message;
		this.isActive = isActive;
		this.status = status;
		this.urlPrint = urlPrint;
	}
	private String message;
    private Boolean isActive;
    private String status;
    private String urlPrint;
    private Boolean isCounter;
}
