package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "m_area_origin")
@Data
public class MAreaOriginEntity {
    @Id
    private String areaOriginId;
    private String areaOriginName;
    private String status;
}
