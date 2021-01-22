package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "t_vendor_bank_person")
@Data
public class TVendorBankPersonEntity {

    @Id
    private Integer id;
    private String switcherCode;
    private String name;
    private String phone;
    private String email;
    private String jabatan;
    private Timestamp created;
    private Timestamp modified;
    private String userCreated;
    private String userModified;
}
