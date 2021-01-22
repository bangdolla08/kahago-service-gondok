package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "m_product_switcher")
@Data
public class MProductSwitcherEntity {
    @Id
    @GeneratedValue
    private Long productSwCode;
    @ManyToOne
    @JoinColumn(name = "switcherCode")
    private MSwitcherEntity switcherEntity;
    private String bankSettlement;
    private String name;
    private String displayName;
    private Byte startDay;
    private Byte endDay;
    private String lastUser;
    private Timestamp lastUpdate;
    private String operatorSw;
    private Byte status;
    private Integer trxArray;
    private String tipeFix;
    private Integer tarif;
    private Integer serviceType;
    private Integer minWeight;
    private String cutoff;
    private Integer komisi;
    @OneToOne
    @JoinColumn(name= "jenisModa")
    private MModaEntity jenisModa;
    private LocalDate liburStart;
    private LocalDate liburEnd;
    private Integer prioritySeq;
    private Byte autosync;
    private Double pembulatanVolume;
    private Integer maxKgKoli;
    private Integer maxJumlahKoli;
    private Double pembagiVolume;
    private Integer kgSurcharge;
    private Boolean isNextrate;
    private Boolean isLeadtime;
    @OneToMany(mappedBy = "productSwCode")
    private List<TGoodsEntity> tGoodsEntities;
    
}
