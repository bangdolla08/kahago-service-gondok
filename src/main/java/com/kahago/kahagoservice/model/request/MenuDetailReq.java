package com.kahago.kahagoservice.model.request;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
public class MenuDetailReq {
	private Integer menuId;
	private Boolean isWrite;
	private Boolean isRead;
	private Boolean isDelete;
}
