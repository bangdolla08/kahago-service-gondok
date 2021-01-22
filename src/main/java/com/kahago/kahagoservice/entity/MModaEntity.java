package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "m_moda")
@Data
public class MModaEntity {
    @Id
    @GeneratedValue
    private Integer idModa;
    private String namaModa;
    private String deskripsi;
    private Byte flag;
    private String createdBy;
    private Timestamp createdTime;
    private String modifiedBy;
    private Timestamp modifiedTime;
}
