package com.kahago.kahagoservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "m_manifest_pos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MManifestPosEntity {
    @Id
    @GeneratedValue
    private Integer seqid;
    private String userid;
    private String manifestNumber;
    private String transref;
    private Integer status;
    private String sign;
    private Timestamp lastUpdate;
}