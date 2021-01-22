package com.kahago.kahagoservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Builder
@Entity
@Table(name = "t_mutasi")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TMutasiEntity {
    
    private String trxNo;
    private String accountNo;
    private BigDecimal amount;
    private String descr;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private MUserEntity userId;
    private LocalDateTime trxServer;
    private LocalDate trxDate;
    private LocalTime trxTime;
    private String productSwCode;
    private String customerId;
    private BigDecimal saldo;
    private String updateBy;
    private Integer trxType;
    @Id
    @GeneratedValue
    private Integer counterMutasi;
}
