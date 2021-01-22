package com.kahago.kahagoservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Builder
@Entity
@Table(name = "t_discount")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TDiscountEntity {
    @Id
    @GeneratedValue
    private Integer seqid;
    private String noTiket;
    private String discountCode;
    private BigDecimal discountValue;
    private LocalDateTime lastUpdate;
}
