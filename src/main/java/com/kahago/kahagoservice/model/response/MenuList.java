package com.kahago.kahagoservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuList {
    private Integer parentId;
    private Integer menuId;
    private String menuName;
    private String title;
    private String icon;
    private String page;
    private Integer flag;
    private Integer orderNumber;
    private Boolean showInMenu;
    private Boolean isRead;
    private Boolean isWrite;
    private Boolean isDelete;
}
