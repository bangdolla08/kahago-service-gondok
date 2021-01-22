package com.kahago.kahagoservice.model.request;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
public class SurchargeDetailReq {
	private Integer id;
	private Integer start;
	private Integer to;
	private Double persen;
	private Boolean status;
}
