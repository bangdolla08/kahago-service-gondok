package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "m_biaya_admin")
@Data
public class MBiayaAdminEntity {
    @Id
    @GeneratedValue
    private Integer id;
    private String switcherCode;
    private Integer amountMax;
    private Integer biayaAdmin;
}
