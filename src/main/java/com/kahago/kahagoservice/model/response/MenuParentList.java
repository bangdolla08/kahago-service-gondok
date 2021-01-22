package com.kahago.kahagoservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuParentList {
	private Integer menuHeaderId;
	private String menuHeaderName;
	private String badge;
}
