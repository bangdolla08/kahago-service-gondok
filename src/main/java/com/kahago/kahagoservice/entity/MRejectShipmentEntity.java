package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "m_reject_shipment")
@Data
public class MRejectShipmentEntity {
    @Id
    @GeneratedValue
    private Integer id;
    private String bookingCode;
    private String reason;
    private Timestamp created;
    private Timestamp modified;
    private String userCreated;
    private String userModified;
}
