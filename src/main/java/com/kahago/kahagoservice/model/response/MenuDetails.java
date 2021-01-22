package com.kahago.kahagoservice.model.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuDetails {
	private Integer titleId;
	private Integer idParent;
	private String menuParentName;
	private Integer orderNumber;
	private boolean isMenu;
//	private Integer idMenuTitle;
	private String title;
	private String icon;
	private String page;
	private List<MenuList> listChild;
}
