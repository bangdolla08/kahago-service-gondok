package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "m_vendor_payment")
@Data
public class MVendorPaymentEntity {
    @Id
    @GeneratedValue
    private Integer codeVendor;
    private String vendorName;
}
