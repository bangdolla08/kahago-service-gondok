package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "m_bank")
@Data
public class MBankEntity {
    @Id
    @GeneratedValue
    private String bankId;
    private String name;
    private String lastUser;
    private Timestamp lastUpdate;
    private String bankCode;
    private String imagePath;
}
