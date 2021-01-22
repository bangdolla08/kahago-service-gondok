package com.kahago.kahagoservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Builder
@Entity
@Table(name = "t_credit")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TCreditEntity {
    @Id
    @GeneratedValue
    private Integer seq;
    private String userId;
    private LocalDate tgl;
    private String tglMulai;
    private BigDecimal nominal;
    private String flag;
    private String creditDay;
    private String tiketNo;
    private String tglSelesai;
}
