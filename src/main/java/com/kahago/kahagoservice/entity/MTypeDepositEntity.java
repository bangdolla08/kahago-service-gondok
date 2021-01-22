package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "m_type_deposit")
@Data
public class MTypeDepositEntity {
    @Id
    @GeneratedValue
    private Integer depositTypeId;
    private String depositTypeName;
}
