package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "m_coupon_category_user")
@Data
public class MCouponCategoryUserEntity {
    @Id
    private Integer idCoupon;
    @ManyToOne(cascade=CascadeType.PERSIST)
    @JoinColumn(name="idCategoryUser")
    private MUserCategoryEntity idCategoryUser;
}
