package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "m_shipment")
@Data
public class MShipmentEntity {
    @Id
    private Integer shipmentId;
    private String shipmentName;
}
