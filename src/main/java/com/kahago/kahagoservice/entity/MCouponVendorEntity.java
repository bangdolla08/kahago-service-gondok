package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "m_coupon_vendor")
@Data
public class MCouponVendorEntity {
	@Id
    private Integer idCoupon;
    @ManyToOne(cascade=CascadeType.PERSIST)
    @JoinColumn(name="switcherCode")
    private MSwitcherEntity switcherCode;
}
