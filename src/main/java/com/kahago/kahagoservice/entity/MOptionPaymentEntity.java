package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "m_option_payment")
@Data
public class MOptionPaymentEntity {
    @Id
    @GeneratedValue
    private Integer seqid;
    private String code;
    private String description;
    private String pathImage;
    private Integer codeVendor;
    private String operatorSw;
    private Boolean isPhone;
    private Boolean isActive;
    private BigDecimal minNominal;
    private Boolean isPayment;
    private Boolean isDeposit;
    private LocalTime offTimeStart;
    private LocalTime offTimeEnd;
}
