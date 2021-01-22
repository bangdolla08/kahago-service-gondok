package com.kahago.kahagoservice.model.request;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
public class UserPriorityRequest {
	@NotNull
	private Integer idUserCategory;
	@NotNull
	private Boolean requestOne;
	@NotNull
	private Boolean requestTwo;
	@NotNull
	private Boolean requestThree;
	@NotNull
	private Boolean payLater;
	@NotNull
	private Integer minKiriman;
	@NotNull
	private Boolean isResiAuto;
}
