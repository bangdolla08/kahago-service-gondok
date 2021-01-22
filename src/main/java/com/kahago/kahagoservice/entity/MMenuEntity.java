package com.kahago.kahagoservice.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "m_menu")
@Data
public class MMenuEntity {
    @Id
    @GeneratedValue
    private Integer menuId;
    private String menuName;
    @Column(name = "menu_parent_id")
    private Integer menuParentId;
    private String menuLink;
    private Integer bonew;
    private Integer orderNumber;
    private Integer flag;
    private Boolean showInMenu;
    private String icon;
    private String pageUrl;
    private String createBy;
    private LocalDateTime createDate;
    private String updateBy;
    private LocalDateTime updateDate;
}
