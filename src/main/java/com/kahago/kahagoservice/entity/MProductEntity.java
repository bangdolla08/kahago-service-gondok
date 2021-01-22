package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "m_product")
@Data
public class MProductEntity {
    @Id
    private Long productCode;
    private String name;
    private String lastUser;
    private Timestamp lastUpdate;
    private String displayName;
    private String jenisProduct;
    private String tipePrior;
}
