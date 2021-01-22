package com.kahago.kahagoservice.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_lion")
public class TLionEntity {
    @Id
    private Integer seq;
    private String tlc;
    private String orgName;
    private String orgDest;
}
