package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_manifest")
@Data
public class TManifestEntity {
    @Id
    private Long seqid;
    private String manifestId;
    private String bookingCode;
}
