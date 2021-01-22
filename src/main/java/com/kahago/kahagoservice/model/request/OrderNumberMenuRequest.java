package com.kahago.kahagoservice.model.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@JsonSerialize
public class OrderNumberMenuRequest {
	private Integer menuIdTitle;
	private Integer menuIdParent;
	private Integer menuId;
	private Integer orderNumber;
}
