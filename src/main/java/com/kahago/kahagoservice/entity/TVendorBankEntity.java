package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "t_vendor_bank")
@Data
public class TVendorBankEntity {

    @Id
    private Integer id;
    private String switcherCode;
    private String bankId;
    private String accountNo;
    private String accountName;
    private Timestamp created;
    private Timestamp modified;
    private String userCreated;
    private String userModified;
}
