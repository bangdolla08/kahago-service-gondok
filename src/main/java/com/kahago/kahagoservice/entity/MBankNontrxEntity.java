package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "m_bank_nontrx")
@Data
public class MBankNontrxEntity {
    @Id
    @GeneratedValue
    private Integer id;
    private String bankCode;
    private String accNo;
    private String accName;
    private Long balance;
    private Integer accountType;
    private Timestamp created;
    private Timestamp modified;
    private Integer status;
    private String userCreated;
    private String userModified;
}
