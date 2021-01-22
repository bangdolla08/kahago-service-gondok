package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_area")
@Data
public class TAreaEntity {
    @Id
    @GeneratedValue
    private Long seqid;
    @ManyToOne
    @JoinColumn(name = "switcherCode")
    private MSwitcherEntity vendor;
    @ManyToOne
    @JoinColumn(name="areaId")
    private MPostalCodeEntity areaId;
    private String areaSwitcher;
    private Boolean status;
    private String productSwitcher;
    private BigDecimal tarif;
    private Integer startDay;
    private Integer endDay;
    private LocalDateTime lastUpdate;
    private String areaOriginId;
    private Integer minimumKg;
    private Integer limitMinimum;
    private BigDecimal nextRate;
    @ManyToOne
    @JoinColumn(name = "productSwCode")
    private MProductSwitcherEntity productSwCode;
}
