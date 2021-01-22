package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "t_goods")
@Data
public class TGoodsEntity {
    @Id
    @GeneratedValue
    private Integer seqid;
    @ManyToOne
    @JoinColumn(name="goodsId")
    private MGoodsEntity goodsId;
    @ManyToOne
    @JoinColumn(name = "productSwCode")
    private MProductSwitcherEntity productSwCode;
    private Byte status;
    private Byte flagSurcharge;
    private String addinformation;
    private String createdBy;
    private Timestamp createdDate;
    private String updateBy;
    private Timestamp updateDate;
    private Integer liabilityValue;
    private Byte isEditLiability;
}
