package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "m_jenis_product")
@Data
public class MJenisProductEntity {
    @Id
    private String jenisProduct;
    private String namaJenis;
    private String tipeProduct;
    private String lastUser;
    private Timestamp lastUpdate;
}
