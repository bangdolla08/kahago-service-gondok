package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.awt.print.Book;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "m_vendor_area")
@Data
public class MVendorAreaEntity {
    @Id
    @GeneratedValue
    private Long seqid;
    @ManyToOne(cascade=CascadeType.PERSIST)
    @JoinColumn(name="areaId")
    private MAreaDetailEntity areaId;
    @ManyToOne(cascade=CascadeType.PERSIST)
    @JoinColumn(name="postalCodeId")
    private MPostalCodeEntity postalCodeId;
    private String requestName;
    private String sendRequest;
    private Integer switcherCode;
    private LocalDateTime lastupdate;
    private Byte flagOrigin;
    private Integer status;
    private String updateBy;
    private Boolean isCheck;
}
