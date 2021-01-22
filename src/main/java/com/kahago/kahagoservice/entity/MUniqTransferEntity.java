package com.kahago.kahagoservice.entity;

import lombok.Data;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "m_uniq_transfer")
@Data
public class MUniqTransferEntity {
    @Id
    @GeneratedValue
    private Integer seqid;
    private BigDecimal nominal;
    private Integer status;
}
