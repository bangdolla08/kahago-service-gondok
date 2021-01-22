package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "m_coupon_option_payment")
@Data
public class MCouponOptionPaymentEntity {
    @Id
    private Integer idCoupon;
    @ManyToOne(cascade=CascadeType.PERSIST)
    @JoinColumn(name="optionPaymentId")
    private MOptionPaymentEntity optionPaymentId;
}
