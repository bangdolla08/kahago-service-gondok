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


@Builder
@Entity
@Table(name = "t_hpp_payment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class THppPaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer seqid;
    @ManyToOne()
    @JoinColumn(name = "bookingCode")
    private TPaymentEntity bookingCode;
    private BigDecimal hpp;
    private BigDecimal hppActual;
    private LocalDateTime lastUpdate;
}
