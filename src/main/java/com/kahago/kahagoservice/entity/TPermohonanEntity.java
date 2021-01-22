package com.kahago.kahagoservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "t_permohonan")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TPermohonanEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer seqid;
    @ManyToOne()
    @JoinColumn(name="nomorPermohonan")
    private MPermohonanEntity nomorPermohonan;
    @ManyToOne()
    @JoinColumn(name = "bookingCode")
    private TPaymentEntity bookingCode;
    private String noInv;
    private BigDecimal amountVendor;
    private BigDecimal hppActual;
    private Integer status;
    private String reason;
    private String lastUser;
    private LocalDateTime lastUpdate;
}
