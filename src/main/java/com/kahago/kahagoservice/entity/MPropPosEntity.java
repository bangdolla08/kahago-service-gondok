package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "m_prop_pos")
@Data
public class MPropPosEntity {
    @Id
    @GeneratedValue
    private Integer seqid;
    private Integer counterManifest;
    private Integer counterResi;
    private Integer counterKantong;
    private String officeCode;
    private String parentPos;
    private String agenid;
}
