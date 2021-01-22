package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "m_manifest")
@Data
public class MManifestEntity {
    @Id
    private String manifestId;
    private Integer courierId;
    private Timestamp lastUpdate;
    private String lastUser;
}
