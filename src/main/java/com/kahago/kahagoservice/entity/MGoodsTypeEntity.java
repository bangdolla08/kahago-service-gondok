package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

@Entity
@Table(name = "m_goods_type")
@Data
public class MGoodsTypeEntity {
    @Id
    @GeneratedValue
    private Long goodsTypeId;
    private String goodsTypeName;
    private String createdBy;
    private Date createdDate;
    private String updateBy;
    private Date updateDate;
}
