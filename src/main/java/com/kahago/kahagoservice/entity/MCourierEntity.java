package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

@Entity
@Table(name = "m_courier")
@Data
public class MCourierEntity {
    @Id
    private Integer courierId;
    private String courierName;
    private String courierPhone;
    private String areaId;
    private String idCard;
    private String typeIdCard;
    private String address;
    private Date createdDate;
    private String createdBy;
    private Date updateDate;
    private String updateBy;
}
