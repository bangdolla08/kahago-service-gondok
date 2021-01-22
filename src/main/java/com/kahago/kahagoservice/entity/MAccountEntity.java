package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "m_account")
@Data
public class MAccountEntity {
    @Id
    private String accountNo;
    private String officeCode;
    private String name;
    private String pob;
    private Date dob;
    private String idType;
    private String idNo;
    private String addrStreet;
    private String addrRt;
    private String addrRw;
    private String addrDstrc;
    private String addrSubdstrc;
    private String addrCity;
    private String addrPostcode;
    private String email;
    private String telp;
    private String hp1;
    private String hp2;
    private String fax;
    private String idBank;
    private String accBank;
    private String accName;
    private String accOffice;
    private String maritalStatus;
    private String sex;
    private String education;
    private String siupNo;
    private String taxNo;
    private String nameCp;
    private String telpCp;
    private String hpCp;
    private String emailCp;
    private String ymCp;
    private String typeCp;
    private String idCp;
    private Integer balance;
    private String depositType;
    private String caCode;
    private String accountType;
    private String agenType;
    private String parentAccount;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
    private String blockStatus;
    private String officeBank;
}
