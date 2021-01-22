package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "m_area")
@Data
public class MAreaEntity {
    @Id
    private String areaId;
    private String areaName;
    private Date createdDate;
    private String createdBy;
    private Date updateDate;
    private String updateBy;
    private Boolean status;
    @OneToOne
    @JoinColumn(name = "areaKotaId")
    private MAreaKotaEntity kotaEntity;
}
