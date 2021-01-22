package com.kahago.kahagoservice.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Table(name = "m_menu_parent")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MMenuParentEntity {
    @Id
    @GeneratedValue
    private Integer menuParentId;
    private String menuParentName;
    private String idBadge;
    @Column(name = "id_menu_title")
    private Integer idMenuTitle;
    private Integer orderNumber;
    private String icon;
    private String pageUrl;
    private String createBy;
    private LocalDateTime createDate;
    private String updateBy;
    private LocalDateTime updateDate;
    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "menu_parent_id", referencedColumnName = "menuParentId")
    private List<MMenuEntity> menus;
}
