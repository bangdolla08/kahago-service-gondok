package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author BangDolla08
 * @created 14/09/20-September-2020 @at 09.02
 * @project kahago-service
 */

@Entity
@Table(name = "m_menu_title")
@Data
public class MMenuTitleEntity {
    @Id
    @GeneratedValue
    private Integer id;
    private String title;
    private Boolean status;
    private Integer orderNumber;
    private String createBy;
    private LocalDateTime createDate;
    private String updateBy;
    private LocalDateTime updateDate;
    @OneToMany(fetch=FetchType.EAGER,orphanRemoval = true,cascade = CascadeType.ALL)
    @JoinColumn(name="id_menu_title", referencedColumnName="id")
    private List<MMenuParentEntity> parentEntities;
}
