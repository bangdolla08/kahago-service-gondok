package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "m_postal_code")
@Data
public class MPostalCodeEntity {
    @Id
    @GeneratedValue
    private Integer idPostalCode;
    @ManyToOne
    @JoinColumn(name = "areaDetailId")
    private MAreaDetailEntity kecamatanEntity;
    private String postalCode;
    private String typeKelurahan;
    private String kelurahan;
    private String kecamatan;
    private String kota;
    private String provinsi;
    private String createBy;
    private LocalDateTime createDate;
    private String updateBy;
    private LocalDateTime updateDate;
    private Integer hitFlag;
}
