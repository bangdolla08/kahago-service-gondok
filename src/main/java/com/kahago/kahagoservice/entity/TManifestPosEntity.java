package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_manifest_pos")
@Data
public class TManifestPosEntity {
    @Id
    private Long seqid;
    private String manifestPos;
    private String no1;
    private String stt1;
    private String weight1;
    private String no2;
    private String stt2;
    private String weight2;
    private String no3;
    private String stt3;
    private String weight3;
}
