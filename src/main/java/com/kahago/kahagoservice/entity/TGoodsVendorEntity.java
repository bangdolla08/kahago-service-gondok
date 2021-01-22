package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_goods_vendor")
@Data
public class TGoodsVendorEntity {
    @Id
    private Integer seq;
    private Integer goodsId;
    private Integer switcherCode;
    private String insuranceFlag;
    private String insuranceValue;
    private String packFlag;
    private String packValue;
}
