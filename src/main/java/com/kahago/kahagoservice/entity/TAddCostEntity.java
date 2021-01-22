package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_add_cost")
@Data
public class TAddCostEntity {
    @Id
    private Integer seqid;
    private Integer costId;
    private String goodsId;
}
