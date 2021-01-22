package com.kahago.kahagoservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author BangDolla08
 * @created 14/09/20-September-2020 @at 10.32
 * @project kahago-service
 */

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuTitle {
    private Integer idMenuTitle;
    private Integer orderNumber;
    private String section;
    private List<MenuDetails> submenu;
}
