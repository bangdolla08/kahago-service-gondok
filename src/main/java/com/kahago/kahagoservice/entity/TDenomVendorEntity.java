package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_denom_vendor")
@Data
public class TDenomVendorEntity {
    @Id
    private Integer seqid;
    private Integer switcherCode;
    private Integer areaId;
    private String areaSwitcher;
    private String status;
}
