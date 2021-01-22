package com.kahago.kahagoservice.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "m_group_list")
@Data
@NoArgsConstructor
public class MGroupListEntity {
	public MGroupListEntity(MGroupListEntity group) {
		this.groupListId = group.getGroupListId();
		this.menuId = group.getMenuId();
		this.userCategory = group.getUserCategory();
		this.isDelete = group.getIsDelete();
		this.isRead = group.getIsRead();
		this.isWrite = group.getIsWrite();
		this.menuParentId = group.getMenuId().getMenuParentId();
		this.boNew=group.getMenuId().getBonew();
	}
    @Id
    @GeneratedValue
    private Integer groupListId;
    @ManyToOne
    @JoinColumn(name = "menuId")
    private MMenuEntity menuId;
    private Integer userCategory;
    private Boolean isRead;
    private Boolean isWrite;
    private Boolean isDelete;
    @Transient
    private Integer menuParentId;
    @Transient
    private Integer boNew;
}
