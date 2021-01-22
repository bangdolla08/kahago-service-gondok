package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "m_area_detail")
@Data
public class MAreaDetailEntity {
    @Id
    @GeneratedValue
    private Integer areaDetailId;
    @ManyToOne
    @JoinColumn(name = "areaKotaId")
    private MAreaKotaEntity kotaEntity;
    private String areaId;
    private String kota;
    private String kecamatan;
    private String province;
    private String status;
    private String isPush;
    private String createBy;
    private LocalDateTime createDate;
    private String updateBy;
    private LocalDateTime updateDate;
}
