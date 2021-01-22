package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "t_fee_trx")
@Data
public class TFeeTrxEntity {
    @Id
    private Integer seqid;
    @Column(name="id_m_fee_trx")
    private Integer idMFeeTrx;
    @Column(name="id_m_user_category")
    private Integer idMUserCategory;
    private Integer fee;
    private String status;
    private String lastUser;
    private Timestamp lastUpdate;
}
