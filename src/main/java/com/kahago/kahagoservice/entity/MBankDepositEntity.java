package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "m_bank_deposit")
@Data
public class MBankDepositEntity {
    @Id
    @GeneratedValue
    private Integer bankDepCode;
    @ManyToOne
    @JoinColumn(name = "bankId")
    private MBankEntity bankId;
    private String accNo;
    private String accName;
    private String lastUser;
    private Timestamp lastUpdate;
    private String status;
    private String depositType;
    private Integer balance;
    private Integer minNominal;
    private Boolean isRobot;
    private Boolean isBank;
}
