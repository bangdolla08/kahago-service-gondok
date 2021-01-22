package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "m_freedate")
@Data
public class MFreedateEntity {
    @Id
    @GeneratedValue
    private Integer seq;
    private Integer tahun;
    private Integer bulan;
    private Integer tgl;
    private String description;
}
