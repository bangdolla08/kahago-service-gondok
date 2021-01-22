package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "m_prop")
@Data
public class MPropEntity {
    @Id
    @GeneratedValue
    private Integer idProp;
    private String version;
    private Byte flag;
    private Byte status;
    private Byte mitraStatus;
    private Timestamp releaseDate;
    private Integer platform;
}
