package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "m_goods")
@Data
public class MGoodsEntity {
    @Id
    @GeneratedValue
    private Long goodsId;
    private String goodsName;
    private Long goodsTypeId;
    private Boolean insuranceFlag;
    private BigDecimal insuranceValue;
    private Boolean packFlag;
    private String packValue;
    private String description;
    private String createdBy;
    private Date createdDate;
    private String updateBy;
    private Date updateDate;
    @OneToMany
    @JoinColumn(name = "goodsId")
    private List<TGoodsEntity> goodsEntityList;
}
