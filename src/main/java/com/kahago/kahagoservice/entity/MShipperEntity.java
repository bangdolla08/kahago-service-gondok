package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

@Entity
@Table(name = "m_shipper")
@Data
public class MShipperEntity {
    @Id
    private String shipperId;
    private String shipperName;
    private String shipperPhone;
    private String areaId;
    private Date createdDate;
    private String createdBy;
    private Date updateDate;
    private String updateBy;
}
