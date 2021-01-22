package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "m_office_bank")
@Data
public class MOfficeBankEntity {
    @Id
    private String officeBank;
    private String parrentOfficeBank;
    private String name;
    private String unitType;
    private String address;
    private String city;
    private String postalCode;
    private String telp;
    private String fax;
    private String statusLayanan;
    private String lastUser;
    private Timestamp lastUpdate;
}
