package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "m_fee_trx")
@Data
public class MFeeTrxEntity {
    @Id
    @GeneratedValue
    private Integer seqid;
    private String trxName;
    private String lastUser;
    private Byte flag;
    private Integer status;
    private Timestamp lastUpdate;
}
