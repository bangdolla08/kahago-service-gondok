package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "t_fee_reference")
@Data
public class TFeeReferenceEntity {
    @Id
    private Integer seqid;
    private String trxNo;
    private Timestamp tglTrx;
    private String userId;
    @ManyToOne
    @JoinColumn(name="unitFee")
    private TFeeTrxEntity unitFee;
    private Integer nominal;
    private Integer feeNominal;
    private Integer feePersen;
    private String lastUser;
    private Timestamp lastUpdate;
}
