package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "m_area_kota")
@Data
public class MAreaKotaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer areaKotaId;
    private String tlc;
    private String title;
    private String name;
    @ManyToOne
    @JoinColumn(name = "areaProvinsiId")
    private MAreaProvinsiEntity provinsiEntity;
    private String createBy;
    private LocalDateTime createDate;
    private String updateBy;
    private LocalDateTime updateDate;
}
