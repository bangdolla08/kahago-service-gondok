package com.kahago.kahagoservice.model.request;
/**
 * @author Ibnu Wasis
 */

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class CategoryOptionPaymentReq {
	@NotNull
	private Integer idUserCategory;
	@NotNull
	private Integer optionPaymentId;
}
