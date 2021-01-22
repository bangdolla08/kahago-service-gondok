package com.kahago.kahagoservice.model.request;

import java.util.List;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
public class MenuSettingRequest {
	private Integer userCategory;
	private List<MenuDetailReq> menu;
	private List<OrderNumberMenuRequest> orderNumber;
}
