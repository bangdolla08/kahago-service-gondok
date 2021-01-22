package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "m_couverage_area")
@Data
public class MCouverageAreaEntity {
    @Id
    private Integer idCouverage;
    private String areaId;
    private String kota;
}
