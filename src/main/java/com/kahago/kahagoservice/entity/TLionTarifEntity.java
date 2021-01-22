package com.kahago.kahagoservice.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_lion_tarif")
public class TLionTarifEntity {
    @Id
    private Integer seq;
    private String tlc;
    private String destCity;
    private String destRoute;
    private String isCity;
}
