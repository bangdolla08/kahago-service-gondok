package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "m_counter")
@Data
public class MCounterEntity {
    @Id
    @GeneratedValue
    private Integer id;
    private Integer tiket;
    private Integer warehouse;
    private Integer outgoingCounter;
}
