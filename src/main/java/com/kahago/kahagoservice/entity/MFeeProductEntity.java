package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "m_fee_product")
@Data
public class MFeeProductEntity {
    @Id
    private Integer feeProductCode;
    private String productSwCode;
    private String caCode;
    private Integer feeAdmin;
    private Integer feeSwitcher;
    private String feeTypeMitra;
    private Integer feeMitra;
    private String lastUser;
    private Timestamp lastUpdate;
    private Integer buyPrice;
    private Integer sellPrice;
    private Integer feeInternal;
    private String status;
    private Integer quota;
    private String productDstCode;
    private Long quotaTrx;
}
