package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "m_collecting_agent")
@Data
public class MCollectingAgentEntity {
    @Id
    private String caCode;
    private String caName;
    private String headerResi;
    private String registerUser;
    private Timestamp registerDate;
    private String lastUser;
    private Timestamp lastUpdate;
    private String bankId;
    private Integer ppn;
    private Integer pph;
    private Integer limitFeeTrf;
    private String masterCa;
    private String feeType;
    private String stsLayanan;
    private String blockStatus;
    private String footer;
}
